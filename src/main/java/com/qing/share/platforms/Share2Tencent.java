package com.qing.share.platforms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qing.log.MLog;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.ShareObject;
import com.qing.share.content.TextObject;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by zwq on 2015/11/04 17:45.<br/><br/>
 */
public class Share2Tencent extends SharePlatform implements IUiListener {

    private static final String TAG = Share2Tencent.class.getName();

    private Tencent mTencent;
    private Bundle messageBundle;
    private int mExtarFlag;
    private int currentMessageObjectType;

    private Share2Tencent(Context context) {
        super(context);
        platform_prefix = "qq_";
    }

    public static Share2Tencent getInstance(Context context) {
        return new Share2Tencent(context);
    }

    @Override
    public void initAllRes() {
        registerToClient();
    }

    @Override
    public SharePlatformType getPlatformType() {
        return sendMessageToWhere == 0 ? SharePlatformType.QQ : SharePlatformType.QZONE;
    }

    @Override
    public void registerToClient() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(appKey, mContext);
        }
    }

    @Override
    public boolean isAppInstalled() {
        return mTencent.isSupportSSOLogin(getActivity());
    }

    @Override
    public void authorize(int type) {

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

    @Override
    public void setSendMessageToWhere(int where) {
        if (where == 0 || where == 1) {
            sendMessageToWhere = where;
            mExtarFlag = (where == 0 ? QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE : QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
    }

    @Override
    public void shareApp(TextObject textObject) {
        Bundle textBundle = new Bundle();
        textBundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        textBundle.putString(QQShare.SHARE_TO_QQ_TITLE, textObject.getTitle());

        textBundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, textObject.getActionUrl());
        textBundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, textObject.getText());
//        textBundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
//        textBundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, getAppName());
//        textBundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        messageBundle = textBundle;
        currentMessageObjectType = textObject.getObjcetType();
    }

    @Override
    public void shareText(TextObject textObject) {
        Bundle textBundle = new Bundle();
        textBundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        textBundle.putString(QQShare.SHARE_TO_QQ_TITLE, textObject.getTitle());
        textBundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, textObject.getActionUrl());

        textBundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, textObject.getText());
//        textBundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");

//        textBundle.putString(QQShare.SHARE_TO_QQ_APP_NAME,  getAppName());
//        textBundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        messageBundle = textBundle;
        currentMessageObjectType = textObject.getObjcetType();
    }

    @Override
    public void shareImage(ImageObject imageObject) {
        //分享纯图片，只能分享本地图片，不能分享网络图片
        Bundle imageBundle = new Bundle();
        imageBundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        if (imageObject.getPath() != null) {
            imageBundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageObject.getPath());

        }else if(imageObject.getUrl() != null){
            imageBundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageObject.getUrl());

            imageBundle.putString(QQShare.SHARE_TO_QQ_TITLE, imageObject.getTitle());
            imageBundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, imageObject.getDescription());
            imageBundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, imageObject.getActionUrl());
        }

//        imageBundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, getAppName());
//        imageBundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        messageBundle = imageBundle;
        currentMessageObjectType = imageObject.getObjcetType();
    }

    @Override
    public void shareWebPage(MediaObject mediaObject) {
        unsupported();
    }

    @Override
    public void shareMusic(MediaObject mediaObject) {
        Bundle musicBundle = new Bundle();
        musicBundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        String audioUrl = mediaObject.getDataUrl();
        if (TextUtils.isEmpty(audioUrl)) {
            throw new IllegalArgumentException("音乐地址不能为空");
        }
        musicBundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
        musicBundle.putString(QQShare.SHARE_TO_QQ_TITLE, mediaObject.getTitle());
        musicBundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mediaObject.getActionUrl());

        musicBundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mediaObject.getThumbUrl());//缩略图
        musicBundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, mediaObject.getDescription());

//        musicBundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, getAppName());
//        musicBundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        messageBundle = musicBundle;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    @Override
    public void shareVideo(MediaObject mediaObject) {
        Bundle videoBundle = new Bundle();
        videoBundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO);
        videoBundle.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, mediaObject.getDataUrl());
        videoBundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mediaObject.getDescription());

        videoBundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, mediaObject.getTitle());

        videoBundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mediaObject.getActionUrl());

        // 支持传多个imageUrl
//        ArrayList<String> imageUrls = new ArrayList<String>();
//        videoBundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        messageBundle = videoBundle;
        currentMessageObjectType = mediaObject.getObjcetType();
    }

    @Override
    public void shareVoice(MediaObject mediaObject) {
        unsupported();
    }

    @Override
    public void sendSingleMessage() {
        registerToClient();
        if (mTencent != null && messageBundle != null) {
            if (currentMessageObjectType == ShareObject.TYPE_IMAGE) {
                String imagePath = messageBundle.getString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL);
                if (TextUtils.isEmpty(imagePath)) {
                    if (mShareListener != null) {
                        mShareListener.onShareFail("图片路径不能为空");
                    }
                    return;
                }

                String imageUrl = messageBundle.getString(QQShare.SHARE_TO_QQ_IMAGE_URL);
                if (!TextUtils.isEmpty(imageUrl)) {
                    if (mShareListener != null) {
                        mShareListener.onShareFail("纯图分享只支持本地图片");
                    }
                    return;
                }
            }
            messageBundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, getAppName());
            messageBundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

            if (getPlatformType() == SharePlatformType.QZONE
                    && currentMessageObjectType == ShareObject.TYPE_MEDIA
                    && messageBundle.getInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE) == QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO) {
                mTencent.publishToQzone(getActivity(), messageBundle, this);
            }else{
                mTencent.shareToQQ(getActivity(), messageBundle, this);
            }
        }
    }

    @Override
    public void sendMultiMessage() {
        throw new IllegalArgumentException("Currently does not support the platform");
    }

    @Override
    public void onNewIntent(Intent intent) {
//        MLog.i(TAG, "---onNewIntent---");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        MLog.i(TAG, "---onActivityResult---");
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, this);
        }
    }

    @Override
    public void clearAll() {

    }

    @Override
    public void onComplete(Object object) {
        MLog.i(TAG, "--onComplete--");
        if (mShareListener != null) {
            mShareListener.onShareSuccess(getPlatformType(), null);
        }
    }

    @Override
    public void onError(UiError uiError) {
        MLog.i(TAG, "--onError--");
        if (mShareListener != null) {
            mShareListener.onShareFail(uiError.toString());
        }
    }

    @Override
    public void onCancel() {
        MLog.i(TAG, "--onCancel--");
        if (mShareListener != null) {
            mShareListener.onShareCancel();
        }
    }
}
