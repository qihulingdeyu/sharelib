package com.qing.share.listener;

import android.os.Bundle;

import com.qing.share.platforms.SharePlatformType;

/**
 * Created by zwq on 2015/10/30 17:25.<br/><br/>
 * 分享监听
 */
public interface ShareListener {

    /** 分享成功 */
    void onShareSuccess(SharePlatformType sharePlatformType, Bundle bundle);
    void onShareFail(String msg);
    void onShareCancel();
}
