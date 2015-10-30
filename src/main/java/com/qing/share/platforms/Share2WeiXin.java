package com.qing.share.platforms;

import android.content.Context;

import com.qing.share.AbsSharePlatform;

/**
 * Created by zwq on 2015/10/30 17:16.<br/><br/>
 */
public class Share2WeiXin extends AbsSharePlatform {

    private static final String TAG = Share2WeiXin.class.getName();
    private static Share2WeiXin instance;

    private Share2WeiXin(Context context) {
        super(context);
    }

    public static Share2WeiXin getInstance(Context context) {
        if (instance == null) {
            synchronized (Share2WeiXin.class) {
                if (instance == null) {
                    instance = new Share2WeiXin(context);
                }
            }
        }
        return instance;
    }

}
