package com.qing.share.platforms;

import android.content.Context;
import android.content.Intent;

import com.qing.share.AbsSharePlatform;
import com.qing.share.SharePlatformType;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.TextObject;

/**
 * Created by zwq on 2015/11/04 17:45.<br/><br/>
 */
public class Share2TengXun extends AbsSharePlatform {

    private static final String TAG = Share2TengXun.class.getName();
    private static Share2TengXun instance;

    private Share2TengXun(Context context) {
        super(context);
    }

    public static Share2TengXun getInstance(Context context) {
        if (instance == null) {
            synchronized (Share2TengXun.class) {
                if (instance == null) {
                    instance = new Share2TengXun(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void initAllRes() {

    }

    @Override
    public SharePlatformType getPlatformType() {
        return sendMessageToWhere == 0 ? SharePlatformType.QQ : SharePlatformType.QZONE;
    }

    @Override
    public void registerToClient() {

    }

    @Override
    public boolean isAppInstalled() {
        return false;
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
        }
    }

    @Override
    public void shareText(TextObject textObject) {

    }

    @Override
    public void shareImage(ImageObject imageObject) {

    }

    @Override
    public void shareWebPage(MediaObject mediaObject) {

    }

    @Override
    public void shareMusic(MediaObject mediaObject) {

    }

    @Override
    public void shareVideo(MediaObject mediaObject) {

    }

    @Override
    public void shareVoice(MediaObject mediaObject) {

    }

    @Override
    public void sendSingleMessage() {

    }

    @Override
    public void sendMultiMessage() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void clearAll() {

    }
}
