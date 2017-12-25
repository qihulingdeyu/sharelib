package com.qing.sharelib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.qing.log.MLog;
import com.qing.qlib.LinearLayoutPage;
import com.qing.share.platforms.SharePlatform;
import com.qing.share.Share;
import com.qing.share.platforms.SharePlatformType;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.ShareObject;
import com.qing.share.content.TextObject;
import com.qing.share.listener.OauthListener;
import com.qing.share.listener.ShareListener;
import com.qing.utils.FileUtil;

/**
 * Created by zwq on 2015/10/31 21:05.<br/><br/>
 */
public class ShareTestPage extends LinearLayoutPage implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = ShareTestPage.class.getName();

    public static final String WX_ID = "wx8b24fa7265b37a21";
    public static final String WX_SECRET = "9834fc0d74deef17ca234fef8d8108eb";
    public static final String SINA_ID = "3521624443";
    public static final String SINA_SECRET = "b027591c63f0d37a73e022b5998674f3";

    public static final String QQ_ID = "1104432153";
    public static final String QQ_SECRET = "vUvWjtvskoEu906P";

    private ShareViewHolder shareViewHolder;
    private Share share;

    public ShareTestPage(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        shareViewHolder = new ShareViewHolder(mContext);
        addView(shareViewHolder.getView(), lParams);
        shareViewHolder.setOnClickListener(this);
        shareViewHolder.setOnCheckedChangeListener(this);


        share = Share.getInstance(getContext());
        share.addShare(SharePlatformType.QQ, QQ_ID, QQ_SECRET, "", null);
        share.addShare(SharePlatformType.WEIXIN, WX_ID, WX_SECRET, "", null);
        share.addShare(SharePlatformType.SINA, SINA_ID, SINA_SECRET, "https://api.weibo.com/oauth2/default.html", null);

        share.setShareListener(new MShareListener());
        share.setOauthListener(new MOauthListener());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onClick(View v) {
//        MLog.i(TAG, "--onClick--");
//        String text = shareViewHolder.input_text.getText().toString();
//        if (TextUtils2.isNullOrEmpty(text)){
//            UIUtil.showToast(getContext(), "文本不能为空");
//            return;
//        }
        if (v == shareViewHolder.clear_btn){
            shareViewHolder.input_text.setText("");
        }else if (v == shareViewHolder.send_btn){
            if (sharePlatform != null) {
                String msg = sharePlatform.getPlatformType().getName();
                        MLog.i(TAG, "onclick single msg:" + msg);
                sharePlatform.setShareListener(new MShareListener());
                sharePlatform.setOauthListener(new MOauthListener());
                sharePlatform.sendSingleMessage();
            }
        }else if (v == shareViewHolder.send_btn2){
            if (sharePlatform != null) {
                String msg = sharePlatform.getPlatformType().getName();
                MLog.i(TAG, "onclick multi msg:" + msg);
                sharePlatform.setShareListener(new MShareListener());
                sharePlatform.setOauthListener(new MOauthListener());
                sharePlatform.sendMultiMessage();
            }
        }else if (v == shareViewHolder.other_btn){
//            if (sharePlatform != null) {
//                sharePlatform.setShareListener(new MShareListener());
//                sharePlatform.authorize();
////                UIUtil.showToast(getContext(), "app install:"+sharePlatform.isAppInstalled());
//            }
            share.show();
        }
    }

    private SharePlatform sharePlatform = null;
    private ShareObject shareObject = null;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        MLog.i(TAG, "--onCheckedChanged--");
        if (isChecked) {
//            if (buttonView == shareViewHolder.QQ){
//                sharePlatform = share.getSharePlatform(SharePlatformType.QQ);
//                sharePlatform.setSendMessageToWhere(0);
//
//            }else if (buttonView == shareViewHolder.QZone){
//                sharePlatform = share.getSharePlatform(SharePlatformType.QQ);
//                sharePlatform.setSendMessageToWhere(1);
//
//            }else if (buttonView == shareViewHolder.WeiXin){
//                sharePlatform = share.getSharePlatform(SharePlatformType.WEIXIN);
//                sharePlatform.setSendMessageToWhere(0);
//
//            }else if (buttonView == shareViewHolder.WeiXin_PYQ){
//                sharePlatform = share.getSharePlatform(SharePlatformType.WEIXIN);
//                sharePlatform.setSendMessageToWhere(1);
//
//            }else if (buttonView == shareViewHolder.Sina){
//                sharePlatform = share.getSharePlatform(SharePlatformType.SINA);
//            }

            if (buttonView == shareViewHolder.text) {
                MLog.i(TAG, "onCheckedChanged  text");
                TextObject textObject = new TextObject();
                textObject.setTitle("title");
                textObject.setText("test/Text");
                textObject.setDescription("描述");
                shareObject = textObject;

            }else if (buttonView == shareViewHolder.image) {
                Bitmap bitmap = FileUtil.getSDBitmap(FileUtil.getSDPath(), "hehua.jpg");
                ImageObject imageObject = new ImageObject();
                imageObject.setDescription("test/Image");
                imageObject.setBitmap(bitmap);
                imageObject.setPath(FileUtil.getSDPath()+ "hehua.jpg");
//                imageObject.setUrl("http://image17-c.poco.cn/jane_admin_img/20151028/PuzzleWall13217thumb1.jpg");
                shareObject = imageObject;

            }else if (buttonView == shareViewHolder.webpage) {
                Bitmap bitmap = FileUtil.getSDBitmap(FileUtil.getSDPath(), "2608.bmp");
                MediaObject mediaObject = new MediaObject();
                mediaObject.setTitle("test/WebPage");
                mediaObject.setDescription("描述");
                mediaObject.setThumb(bitmap);
                mediaObject.setActionUrl("http://www.baidu.com/");
                mediaObject.setDataUrl("http://www.baidu.com/");
                mediaObject.setDefaultText("默认文本");
                shareObject = mediaObject;

            }else if (buttonView == shareViewHolder.music) {

            }else if (buttonView == shareViewHolder.video) {

            }else if (buttonView == shareViewHolder.voice) {

            }
            if(shareObject != null) {
//            if(shareObject != null && sharePlatform != null) {
//                MLog.i(TAG, "onCheckedChanged  sharePlatform type:" + sharePlatform.getPlatformType().getName()+", ObjcetType:"+shareObject.getObjectType());
//                switch (shareObject.getObjectType()) {
//                    case 0:
//                        sharePlatform.shareText((TextObject) shareObject);
//                        break;
//                    case 1:
//                        sharePlatform.shareImage((ImageObject) shareObject);
//                        break;
//                    case 2:
//                        sharePlatform.shareWebPage((MediaObject) shareObject);
//                        break;
//                    default:
//                        break;
//                }

                share.setShareObject(shareObject);
            }
        }

    }

    class MOauthListener implements OauthListener {

        @Override
        public void onOauthSuccess(SharePlatformType sharePlatformType, Bundle bundle) {
            MLog.i(TAG, "---onShareSuccess---" + sharePlatformType.getName());
        }

        @Override
        public void onOauthFail(int code, String msg) {
            MLog.i(TAG, "---onOauthFail---"+msg);
        }

        @Override
        public void onOauthCancel() {
            MLog.i(TAG, "---onOauthCancel---");
        }
    }

    class MShareListener implements ShareListener {
        @Override
        public void onShareSuccess(SharePlatformType sharePlatformType, Bundle bundle) {
            MLog.i(TAG, "---onShareSuccess---"+sharePlatformType.getName());
        }

        @Override
        public void onShareFail(int code, String msg) {
            MLog.i(TAG, "---onShareFail---"+msg);
        }

        @Override
        public void onShareCancel() {
            MLog.i(TAG, "---onShareCancel---");
        }
    }

    @Override
    public boolean onBack() {
        return false;
    }

    @Override
    public boolean onStop() {
        return false;
    }

    @Override
    public boolean onPause() {
        return false;
    }

    @Override
    public boolean onDestroy() {
        return false;
    }

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onResume() {
        return false;
    }

    @Override
    public boolean onPageStateChange(boolean isTop, Object[] params) {
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        MLog.i(TAG, "--onNewIntent-->");
//        if (share != null) {
//            share.onNewIntent(intent);
//        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.i(TAG, "--onActivityResult-->");
//        if (share != null) {
//            share.onActivityResult(requestCode, resultCode, data);
//        }
        return false;
    }

    @Override
    public boolean onActivityKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onActivityKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onRestore() {

    }
}
