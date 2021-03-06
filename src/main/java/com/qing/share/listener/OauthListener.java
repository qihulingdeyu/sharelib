package com.qing.share.listener;

import android.os.Bundle;

import com.qing.share.platforms.SharePlatformType;

/**
 * Created by zwq on 2015/11/02 18:21.<br/><br/>
 * 授权监听
 */
public interface OauthListener {

    public static final int CODE_OTHER = 0;

    /**
     * 授权成功
     */
    void onOauthSuccess(SharePlatformType sharePlatformType, Bundle bundle);

    void onOauthFail(int code, String msg);

    void onOauthCancel();

}
