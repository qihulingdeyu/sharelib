package com.qing.share.platforms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qing.log.MLog;
import com.qing.share.AbsSharePlatform;
import com.qing.share.content.MediaObject;
import com.qing.share.OauthInfo;
import com.qing.share.listener.OauthListener;
import com.qing.share.SharePlatformType;
import com.qing.share.Utils;
import com.qing.utils.StringUtils;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseRequest;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by zwq on 2015/10/30 11:49.<br/><br/>
 * 分享到新浪<br/>
 */
public class Share2Sina extends AbsSharePlatform implements IWeiboHandler.Response, IWeiboHandler.Request {

    private static final String TAG = Share2Sina.class.getName();

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private final int SHARE_CLIENT = 1;
    private final int SHARE_ALL_IN_ONE = 2;
    private int mShareType = 2;

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;
    private WeiboMultiMessage weiboMultiMessage;
    private int currentMessageObjectType;
    private boolean shareSuccess;

    private static Share2Sina instance;

    private Share2Sina(Context context) {
        super(context);
        platform_prefix = "sina_";
    }

    public static Share2Sina getInstance(Context context) {
        if (instance == null) {
            synchronized (Share2Sina.class) {
                if (instance == null) {
                    instance = new Share2Sina(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void initAllRes() {
        initSsoHandler(appKey, redirectUrl, scope);
        registerToClient();
    }

    @Override
    public SharePlatformType getPlatformType() {
        return SharePlatformType.SINA;
    }

    public void initSsoHandler(String appKey, String redirectUrl, String scope) {
        if (mSsoHandler == null) {
            if (mAuthInfo == null){
                mAuthInfo = new AuthInfo(mContext, appKey, redirectUrl, scope);
            }
            mSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
        }
    }

    @Override
    public void registerToClient() {
        if (mWeiboShareAPI == null) {
            // 创建微博分享接口实例
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext, appKey);

            // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
            // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
            // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
            mWeiboShareAPI.registerApp();

            // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
            // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
            // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
            // 失败返回 false，不调用上述回调
//            if (savedInstanceState != null) {
//                mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//            }
            // 处理微博客户端发送过来的请求
            mWeiboShareAPI.handleWeiboRequest(getIntent(), this);
        }
    }

    @Override
    public boolean isAppInstalled() {
        return mSsoHandler.isWeiboAppInstalled();
    }

    @Override
    public void authorize(int type) {
        authorizeType = type;
        isAuthorize = true;
        initSsoHandler(appKey, redirectUrl, scope);
        switch (type) {
            case 0:
                mSsoHandler.authorize(new AuthorListener());
                break;
            case 1:
                mSsoHandler.authorizeWeb(new AuthorListener());
                break;
            case 2:
                mSsoHandler.authorizeClientSso(new AuthorListener());
                break;
            default:
                break;
        }
    }

    @Override
    public void register() {
    }

    /**
     * 手机短信授权
     * title 短信注册页面title，可选，不传时默认为""验证码登录""。此处WeiboAuthListener 对象 listener
     * 可以是和sso 同一个 listener   回调对象 也可以是不同的。开发者根据需要选择
     */
    @Override
    public void registerOrLoginByMobile() {
        isAuthorize = true;
        mSsoHandler.registerOrLoginByMobile("验证码登录", new AuthorListener());
    }
    @Override
    public void login() {
    }

    @Override
    public void logout() {
//        AccessTokenKeeper.clear(mContext);
//        mAccessToken = new Oauth2AccessToken();
//        updateTokenView(false);

//        mAccessToken = AccessTokenKeeper.readAccessToken(WBMLoginMLogoutActivity.this);
//        com.sina.weibo.sdk.openapi.MLogoutAPI api = new MLogoutAPI(mContext, getAppKey(), mAccessToken).logout(mMLogoutListener);
    }

    @Override
    public void setSendMessageToWhere(int where) {

    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
//        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                new java.util.Date(mAccessToken.getExpiresTime()));
//        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
//
//        String message = String.format(format, mAccessToken.getToken(), date);
//        if (hasExisted) {
//            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
//        }
//        mTokenText.setText(message);
    }

    private void initWeiboMultiMessage() {
        if (weiboMultiMessage == null) {
            weiboMultiMessage = new WeiboMultiMessage();
        }
    }

    /**
     * 文本消息对象。
     *
     * @param textObject
     */
    @Override
    public void shareText(com.qing.share.content.TextObject textObject) {
        TextObject sinaTextObject = new TextObject();
        sinaTextObject.text = textObject.getText();

        initWeiboMultiMessage();
        weiboMultiMessage.textObject = sinaTextObject;
        currentMessageObjectType = textObject.getObjcetType();
    }

    /**
     * 图片消息对象。
     * 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     *
     * @param imageObject
     */
    @Override
    public void shareImage(com.qing.share.content.ImageObject imageObject) {
        ImageObject sinaImageObject = new ImageObject();
        sinaImageObject.setImageObject(imageObject.getBitmap());

        initWeiboMultiMessage();
        weiboMultiMessage.imageObject = sinaImageObject;
        currentMessageObjectType = imageObject.getObjcetType();
    }

    @Override
    public void shareWebPage(MediaObject mediaObject) {
        WebpageObject sinaMediaObject = new WebpageObject();
        sinaMediaObject.identify = Utility.generateGUID();
        sinaMediaObject.title = mediaObject.getTitle();
        sinaMediaObject.description = mediaObject.getDescription();

        // 设置 Bitmap 类型的图片到视频对象里
        sinaMediaObject.setThumbImage(mediaObject.getThumb());
        sinaMediaObject.actionUrl = (mediaObject.getTitle()==null?"":mediaObject.getTitle()+"，") + mediaObject.getActionUrl() + (mediaObject.getDescription()==null?"":"，"+mediaObject.getDescription());
        sinaMediaObject.defaultText = mediaObject.getDefaultText();

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = sinaMediaObject;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    @Override
    public void shareMusic(MediaObject mediaObject) {
        if (mediaObject.getDuration() <= 0) {
            mediaObject.setDuration(10);
        }
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = mediaObject.getTitle();
        musicObject.description = mediaObject.getDescription();

        musicObject.setThumbImage(mediaObject.getThumb());
        musicObject.actionUrl = mediaObject.getActionUrl();
        musicObject.dataUrl = mediaObject.getDataUrl();
        musicObject.dataHdUrl = mediaObject.getDataHdUrl();
        musicObject.duration = mediaObject.getDuration();
        musicObject.defaultText = mediaObject.getDefaultText();

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = musicObject;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    @Override
    public void shareVideo(MediaObject mediaObject) {
        if (mediaObject.getDuration() <= 0) {
            mediaObject.setDuration(10);
        }
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = mediaObject.getTitle();
        videoObject.description = mediaObject.getDescription();

        Utils.compressBitmap(mediaObject.getThumb(), 85);

        videoObject.setThumbImage(mediaObject.getThumb());
        videoObject.actionUrl = mediaObject.getActionUrl();
        videoObject.dataUrl = mediaObject.getDataUrl();
        videoObject.dataHdUrl = mediaObject.getDataHdUrl();
        videoObject.duration = mediaObject.getDuration();
        videoObject.defaultText = mediaObject.getDefaultText();

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = videoObject;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    @Override
    public void shareVoice(MediaObject mediaObject) {
        if (mediaObject.getDuration() <= 0) {
            mediaObject.setDuration(10);
        }
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = mediaObject.getTitle();
        voiceObject.description = mediaObject.getDescription();

        voiceObject.setThumbImage(mediaObject.getThumb());
        voiceObject.actionUrl = mediaObject.getActionUrl();
        voiceObject.dataUrl = mediaObject.getDataUrl();
        voiceObject.dataHdUrl = mediaObject.getDataHdUrl();
        voiceObject.duration = mediaObject.getDuration();
        voiceObject.defaultText = mediaObject.getDefaultText();

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = voiceObject;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    @Override
    public void sendSingleMessage() {
        String msg = null;
        if (weiboMultiMessage == null) {
            msg = "WeiboMultiMessage Object is null";
        }else{
            // 1. 初始化微博的分享消息
            // 用户可以分享文本、图片、网页、音乐、视频中的一种
            WeiboMessage weiboMessage = new WeiboMessage();
            if (currentMessageObjectType == 0 && weiboMultiMessage.textObject != null) {
                weiboMessage.mediaObject = weiboMultiMessage.textObject;

            } else if (currentMessageObjectType == 1 && weiboMultiMessage.imageObject != null) {
                weiboMessage.mediaObject = weiboMultiMessage.imageObject;

            } else if (currentMessageObjectType == 2 && weiboMultiMessage.mediaObject != null) {
                weiboMessage.mediaObject = weiboMultiMessage.mediaObject;

            } else {
                weiboMultiMessage = null;
                weiboMessage = null;
            }
            if (weiboMessage == null) {
                msg = "WeiboMessage Object is null";
            }
            if (msg == null) {
                if (weiboMessage != null && weiboMessage.mediaObject != null
                        && weiboMessage.mediaObject.thumbData != null
                        && weiboMessage.mediaObject.thumbData.length > THUMB_MAX_ARRAY_LENGTH) {
                    msg = "thumb size should be within 32KB";
                }
                if (msg == null) {
                    // 2. 初始化从第三方到微博的消息请求
                    SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
                    // 用transaction唯一标识一个请求
                    request.transaction = String.valueOf(System.currentTimeMillis());
                    request.message = weiboMessage;

                    // 3. 发送请求消息到微博，唤起微博分享界面
                    setMessage(request);
                }
            }
        }
        if (msg != null && mShareListener != null) {
            mShareListener.onShareFail(msg);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    @Override
    public void sendMultiMessage() {
        String msg = null;
        if (weiboMultiMessage == null) {
            msg = "WeiboMultiMessage Object is null";
        }else{
            if (weiboMultiMessage.mediaObject != null
                    && weiboMultiMessage.mediaObject.thumbData != null
                    && weiboMultiMessage.mediaObject.thumbData.length > THUMB_MAX_ARRAY_LENGTH) {
                msg = "thumb size should be within 32KB";
            }
            if (msg == null) {
                // 1. 初始化微博的分享消息
                // 2. 初始化从第三方到微博的消息请求
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                // 用transaction唯一标识一个请求
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMultiMessage;

                // 3. 发送请求消息到微博，唤起微博分享界面
                setMessage(request);
            }
        }
        if (msg != null && mShareListener != null) {
            mShareListener.onShareFail(msg);
        }
    }

    private void setMessage(final BaseRequest request) {
        registerToClient();

        MLog.i("bbb", "mShareType：" + mShareType);
        boolean result = true;
        if (mShareType == SHARE_CLIENT) {
            if (isAppInstalled()) {
                result = mWeiboShareAPI.sendRequest(getActivity(), request);
            }else{
                if (mShareListener != null) {
                    mShareListener.onShareFail("未安装客户端");
                }
                return;
            }
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            oauthInfo = getOauthInfo();
            if (oauthInfo != null && !TextUtils.isEmpty(oauthInfo.getExpiresIn())) {
                //uid: 2502195515, access_token: 2.00L3x1jCP541qDc99d824d870U_izt, refresh_token: 2.00L3x1jCP541qD3745f9a8c4xKSAwC, phone_num: ,
                // expires_in: 1604827356269*1000 + 1447147357271 = 1606274503626271
                // 1604827356269 + 1447147357271 = 3051974713540   157679998998
                // 1447147788200
                //检查授权是否过期
//                long t = Long.parseLong("1604827356269");//oauthInfo.getExpiresIn());
//                        String d = StringUtils.getDateTime("yyyyMMdd HHmmss", new Date(t));
//                long ct = new Date().getTime();
//                long abs = System.currentTimeMillis();//t - ct;
//                String d2 = StringUtils.getDateTime("yyyyMMdd HHmmss", new Date(abs));
//                MLog.i("bbb", System.currentTimeMillis()+", t:"+t+", 检查授权是否过期:"+d + ", ct:"+ ct+", abs:"+abs+" d2:"+d2);

            }

            if (oauthInfo == null || TextUtils.isEmpty(oauthInfo.getAccessToken())) {
                final OauthListener temp_OauthListener = mOauthListener;
                mOauthListener = new OauthListener() {
                    @Override
                    public void onOauthSuccess(SharePlatformType sharePlatformType, Bundle bundle) {
                        if (temp_OauthListener != null) {
                            temp_OauthListener.onOauthSuccess(sharePlatformType, bundle);
                        }
                        MLog.i("bbb", "授权成功，继续执行分享...");
                        isAuthorize = false;
                        oauthInfo = getOauthInfo();
                        mWeiboShareAPI.sendRequest(getActivity(), request, mAuthInfo, oauthInfo.getAccessToken(), new AuthorListener());
                    }
                    @Override
                    public void onOauthFail(String msg) {
                        if (temp_OauthListener != null) {
                            temp_OauthListener.onOauthFail(msg);
                        }
                        if (mShareListener != null) {
                            mShareListener.onShareFail("授权失败："+msg);
                        }
                    }
                    @Override
                    public void onOauthCancel() {
                        if (temp_OauthListener != null) {
                            temp_OauthListener.onOauthCancel();
                        }
                        if (mShareListener != null) {
                            mShareListener.onShareFail("取消授权");
                        }
                    }
                };
                authorize();
                return;
            }else{
                result = mWeiboShareAPI.sendRequest(getActivity(), request, mAuthInfo, oauthInfo.getAccessToken(), new AuthorListener());
            }
        }
        if (!result && mShareListener != null) {
            mShareListener.onShareFail("分享失败，可能是未注册Activity：com.sina.weibo.sdk.component.WeiboSdkBrowser");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        if (mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(intent, this);

            // 处理微博客户端发送过来的请求
            mWeiboShareAPI.handleWeiboRequest(intent, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.i("bbb", "--onActivityResult--requestCode:" + requestCode + ", resultCode:"+resultCode);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void clearAll() {

    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onRequest(BaseRequest baseRequest) {
        // 保存从微博客户端唤起第三方应用时，客户端发送过来的请求数据对象
        // baseRequest一直都是空的
        if (!shareSuccess && mShareListener != null) {
            mShareListener.onShareFail("分享失败");
        }
        shareSuccess = false;
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                if (mShareListener != null) {
                    shareSuccess = true;
                    mShareListener.onShareSuccess(getPlatformType(), null);
                }
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                if (mShareListener != null) {
                    mShareListener.onShareCancel();
                }
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                if (mShareListener != null) {
                    mShareListener.onShareFail(baseResp.errCode +", "+ baseResp.errMsg);
                }
                break;
        }
    }

    class AuthorListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle bundle) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
            MLog.i("bbb", mAccessToken.toString());
            //uid: 2502195515, access_token: 2.00L3x1jCP541qDc99d824d870U_izt, refresh_token: 2.00L3x1jCP541qD3745f9a8c4xKSAwC, phone_num: , expires_in: 1604127307737
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                if (isAuthorize && mOauthListener != null) {
                    // 保存 Token 到 SharedPreferences
                    OauthInfo oauthInfo = new OauthInfo();
                    oauthInfo.setUid(mAccessToken.getUid());
                    oauthInfo.setAccessToken(mAccessToken.getToken());
                    oauthInfo.setRefreshToken(mAccessToken.getRefreshToken());
                    oauthInfo.setPhoneNum(mAccessToken.getPhoneNum());
                    oauthInfo.setExpiresIn(mAccessToken.getExpiresTime() + "");//System.currentTimeMillis() + Long.parseLong(mAccessToken.getExpiresTime()) * 1000;
                    setOauthInfo(oauthInfo);

                    isAuthorize = false;
                    mOauthListener.onOauthSuccess(getPlatformType(), bundle);
                }else if (mShareListener != null) {
                    shareSuccess = true;
                    mShareListener.onShareSuccess(getPlatformType(), bundle);
                }
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String message = "code-->"+bundle.getString("code");
                if (isAuthorize && mOauthListener != null) {
                    isAuthorize = false;
                    mOauthListener.onOauthFail("message:"+message);
                }else if (mShareListener != null) {
                    mShareListener.onShareFail("message:"+message);
                }
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {
            if (isAuthorize && mOauthListener != null) {
                isAuthorize = false;
                mOauthListener.onOauthFail(e.getMessage());
            }else if (mShareListener != null) {
                mShareListener.onShareFail(e.getMessage());
            }
        }

        @Override
        public void onCancel() {
            if (isAuthorize && mOauthListener != null) {
                isAuthorize = false;
                mOauthListener.onOauthCancel();
            }else if (mShareListener != null) {
                mShareListener.onShareCancel();
            }
        }
    }

}
