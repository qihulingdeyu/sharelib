package com.qing.share.platforms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.qing.callback.HttpCallback;
import com.qing.log.MLog;
import com.qing.share.SharePlatform;
import com.qing.share.SharePlatformType;
import com.qing.share.Utils;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.TextObject;
import com.qing.utils.ThreadUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by zwq on 2015/10/30 17:16.<br/><br/>
 */
public class Share2WeiXin extends SharePlatform implements IWXAPIEventHandler {

    private static final String TAG = Share2WeiXin.class.getName();

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private SendMessageToWX.Req req;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private final int sendSingleMessage = 1;
    private final int sendMultiMessage = 2;
    /** 0:不发送，1：单条发送，2：多条发送*/
    private int needSendMessage;
    private boolean isLoading;

    private Share2WeiXin(Context context) {
        super(context);
        platform_prefix = "weixin_";
    }

    public static Share2WeiXin getInstance(Context context) {
        return new Share2WeiXin(context);
    }

    @Override
    public void initAllRes() {

        registerToClient();
    }

    @Override
    public SharePlatformType getPlatformType() {
        return sendMessageToWhere == 0 ? SharePlatformType.WEIXIN : SharePlatformType.WEIXIN_PYQ;
    }

    @Override
    public void registerToClient() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(mContext, appKey, false);

        // 将该app注册到微信
        api.registerApp(appKey);

        api.handleIntent(getIntent(), this);
    }

    @Override
    public void unregisterClient() {
        super.unregisterClient();
        api.unregisterApp();
    }

    @Override
    public boolean isAppInstalled() {
        return api.isWXAppInstalled();
    }

    @Override
    public void authorize(int type) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "post_timeline";//需要申请权限
        req.state = "none";
        api.sendReq(req);
    }

    @Override
    public void register() {

    }

    @Override
    public void registerOrLoginByMobile() {

    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    /**
     * 发送到朋友圈还是好友
     * @param where 0：好友，1：朋友圈
     */
    public void setSendMessageToWhere(int where) {
        if (where == 0 || where == 1) {
            sendMessageToWhere = where;
        }
    }

    @Override
    public void shareApp(TextObject textObject) {

    }

    @Override
    public void shareText(TextObject textObject) {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = textObject.getText();

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = textObject.getText();

        // 构造一个Req
        req = new SendMessageToWX.Req();
        req.transaction = Utils.buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
    }

    @Override
    public void shareImage(final ImageObject imageObject) {
        if (imageObject.getBitmap() != null) {
            WXImageObject imgObj = new WXImageObject(imageObject.getBitmap());
            Bitmap bitmap = imageObject.getBitmap();

            shareImage(imgObj, bitmap);

        }else if (imageObject.getPath() != null) {
            WXImageObject imgObj = new WXImageObject();
            imgObj.setImagePath(imageObject.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(imageObject.getPath());

            shareImage(imgObj, bitmap);

        }else if (imageObject.getUrl() != null) {
//            imgObj = new WXImageObject();
//            imgObj.imageUrl = imageObject.getUrl();
//            try {
//                bitmap = BitmapFactory.decodeStream(new URL(imageObject.getUrl()).openStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            shareImage(imgObj, bitmap);
            final HttpCallback httpCallback = new HttpCallback() {
                @Override
                public void onSuccess(String content, InputStream is) {
                    if (is != null) {
                        WXImageObject imgObj = new WXImageObject();
                        imgObj.imageUrl = imageObject.getUrl();

                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        boolean success = shareImage(imgObj, bitmap);
                        MLog.i("bbb", "success:"+success);
                        if (success){
                            isLoading = false;
                            if (needSendMessage == sendSingleMessage) {
                                sendSingleMessage();
                            }else if (needSendMessage == sendSingleMessage) {
                                sendMultiMessage();
                            }
                        }
                    }
                }
                @Override
                public void onFail(String msg) {
                }
            };
            ThreadUtils threadUtils = new ThreadUtils() {
                InputStream is = null;
                @Override
                public void execute() {
                    isLoading = true;
                    if (imageObject.getUrl() != null) {
                        try {
                            is = new URL(imageObject.getUrl()).openStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void finish() {
                    super.finish();
                    httpCallback.dispatchResult(HttpCallback.SUCCESS, "image url", is);
                }
            };
            threadUtils.start();
        }
    }

    private boolean shareImage(WXImageObject imgObj, Bitmap bitmap) {
        boolean cut = true;
        if (bitmap != null && !bitmap.isRecycled()) {
            if (THUMB_MAX_SIZE < bitmap.getWidth() || THUMB_MAX_SIZE < bitmap.getHeight()) {
                int width, height;
                Bitmap temp_bitmap = null;
                if (cut) {
                    int min = Math.min(Math.min(bitmap.getWidth(), bitmap.getHeight()), THUMB_MAX_SIZE);
                    width = min;
                    height = min;
                    temp_bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
                }else{
                    float scaleWH = bitmap.getWidth() * 1.0f / bitmap.getHeight();
                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        width = THUMB_MAX_SIZE;
                        height = (int) (width / scaleWH);
                    }else{
                        height = THUMB_MAX_SIZE;
                        width = (int) (height * scaleWH);
                    }
                    temp_bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                }
                bitmap.recycle();
                bitmap = temp_bitmap;
            }

            if (bitmap == null || bitmap.isRecycled()) {
                if (mShareListener != null) {
                    mShareListener.onShareFail("bitmap is null");
                }
                imgObj = null;
                return false;
            }
//            FileUtils.write2SD(bitmap, FileUtils.getSDPath()+"123.jpg", true, false);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
            msg.thumbData = Utils.bitmapToByteArray(bitmap, true);  // 设置缩略图 The file size should be within 32KB.

            req = new SendMessageToWX.Req();
            req.transaction = Utils.buildTransaction("img");
            req.message = msg;
            return true;
        }
        return false;
    }

    @Override
    public void shareWebPage(MediaObject mediaObject) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mediaObject.getDataUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mediaObject.getTitle();
        msg.description = mediaObject.getDescription();

        msg.thumbData = Utils.bitmapToByteArray(mediaObject.getThumb(), true);

        req = new SendMessageToWX.Req();
        req.transaction = Utils.buildTransaction("webpage");
        req.message = msg;
    }

    @Override
    public void shareMusic(MediaObject mediaObject) {
        WXMusicObject music = new WXMusicObject();
        //music.musicUrl = "http://www.baidu.com";
        //music.musicUrl="http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
        //music.musicUrl="http://120.196.211.49/XlFNM14sois/AKVPrOJ9CBnIN556OrWEuGhZvlDF02p5zIXwrZqLUTti4o6MOJ4g7C6FPXmtlh6vPtgbKQ==/31353278.mp3";
        //1
        music.musicUrl = mediaObject.getDataUrl();
        //2
//        music.musicLowBandUrl = "http://www.qq.com";

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = mediaObject.getTitle();
        msg.description = mediaObject.getDescription();
        msg.thumbData = Utils.bitmapToByteArray(mediaObject.getThumb(), true);

        req = new SendMessageToWX.Req();
        req.transaction = Utils.buildTransaction("music");
        req.message = msg;
    }

    @Override
    public void shareVideo(MediaObject mediaObject) {
        WXVideoObject video = new WXVideoObject();
        //1
        video.videoUrl = mediaObject.getDataUrl();
        //2
//        video.videoLowBandUrl = "http://www.qq.com";

        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = mediaObject.getTitle();
        msg.description = mediaObject.getDescription();
        //1
        msg.thumbData = Utils.bitmapToByteArray(mediaObject.getThumb(), true);

        req = new SendMessageToWX.Req();
        req.transaction = Utils.buildTransaction("video");
        req.message = msg;
    }

    @Override
    public void shareVoice(MediaObject mediaObject) {
        unsupported();
    }

    @Override
    public synchronized void sendSingleMessage() {
//        MLog.i("bbb", "11111");
        needSendMessage = sendSingleMessage;
        if (isLoading)
            return;
        needSendMessage = 0;
//        MLog.i("bbb", "2222");
        String msg = null;
        if (api != null && req != null) {
            /*判断是发送给好友  还是发送到朋友圈
            SendMessageToWX.Req.WXSceneTimeline 朋友圈
            SendMessageToWX.Req.WXSceneSession 好友
            */
            req.scene = SendMessageToWX.Req.WXSceneSession;
            if (sendMessageToWhere == 1) {
                if (api.getWXAppSupportAPI() >= TIMELINE_SUPPORTED_VERSION) {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                }else{
                    msg = "this version of weixin is unsupport share to pengyouquan";
                }
            }
//            MLog.i("bbb", "scene:"+req.scene+", type:"+req.message.getType());
            if (msg == null && req.message != null && req.message.thumbData != null) {
                if (req.message.thumbData.length > THUMB_MAX_ARRAY_LENGTH) {
                    msg = "thumb size should be within 32KB";
                }
            }
            // 调用api接口发送数据到微信
            if (msg == null && api.sendReq(req) == false) {
                msg = "sendRequest msg fail: unknow error";
            }
            req = null;
        }else{
            msg = "IWXAPI object or SendMessageToWX.Req object is null";
        }
        if (msg != null && mShareListener != null) {
            mShareListener.onShareFail(msg);
        }
    }

    @Override
    public synchronized void sendMultiMessage() {
        needSendMessage = sendMultiMessage;
        if (isLoading)
            return;
        needSendMessage = 0;
        if (mShareListener != null) {
            mShareListener.onShareFail("微信没有发送多种信息的功能，此方法未实现");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        MLog.i("bbb", "--onNewIntent--:");
        getActivity().setIntent(intent);
        if (api != null) {
            api.handleIntent(intent, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.i("bbb", "--onActivityResult--requestCode:"+requestCode+", resultCode:"+resultCode);
        switch (requestCode) {
            case 0x101:

//                final WXAppExtendObject appdata = new WXAppExtendObject();
//                final String path = Utils.getResultPhotoPath(mContext, data, FileUtils.getSDPath() + "sharelib/");
//                appdata.filePath = path;
//                appdata.extInfo = "this is ext info";
//
//                final WXMediaMessage msg = new WXMediaMessage();
//                msg.setThumbImage(Utils.extractThumbNail(path, 150, 150, true));
//                msg.title = "this is title";
//                msg.description = "this is description";
//                msg.mediaObject = appdata;
//
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = Utils.buildTransaction("appdata");
//                req.message = msg;
//
//                sendSingleMessage();
                break;
            default:
                break;
        }
    }

    @Override
    public void clearAll() {

    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        MLog.i("bbb", "--onReq--getType:"+baseReq.getType());
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        MLog.i("bbb", "--onResp--errCode:"+baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
                break;
            default:
//                result = R.string.errcode_unknown;
                break;
        }
    }
}
