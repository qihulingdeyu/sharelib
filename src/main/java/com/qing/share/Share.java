package com.qing.share;

import android.content.Context;
import android.content.Intent;

import com.qing.log.MLog;
import com.qing.share.listener.ShareListener;
import com.qing.share.platforms.Share2Sina;
import com.qing.share.platforms.Share2TengXun;
import com.qing.share.platforms.Share2WeiXin;
import com.qing.share.ui.ShareView;

import java.util.HashMap;

/**
 * Created by zwq on 2015/08/26 17:07.<br/><br/>
 */
public class Share {

    private static final String TAG = Share.class.getName();
    private static Share instance;
    private Context mContext;
    private static ShareView shareView;
    private static HashMap<String, AbsSharePlatform> shareList;
    private static HashMap<SharePlatformType, KeyInfo> keyInfoList;
    private SharePlatformType currentPlatformType;

    public static Share getInstance(Context context){
        if(instance==null){
            synchronized (Share.class){
                if (instance==null){
                    instance = new Share(context);
                }
            }
        }
        return instance;
    }

    private Share(){}
    private Share(Context context){
        mContext = context;
        shareList = new HashMap<>();
        keyInfoList = new HashMap<>();
        MLog.setDebugMode(true);
    }

    public void addShare(SharePlatformType sharePlatformType, String appKey, String appSecret, String redirectUrl, String scope){
        if (!shareList.containsKey(sharePlatformType.getName())) {
            AbsSharePlatform platform = null;
            switch (sharePlatformType){
                case QQ:
                    platform = Share2TengXun.getInstance(mContext);
                    break;
                case WEIXIN:
                    platform = Share2WeiXin.getInstance(mContext);
                    break;
                case SINA:
                    platform = Share2Sina.getInstance(mContext);
                    break;
                default:
                    break;
            }
            if (platform != null) {
                platform.setShareConfig(appKey, appSecret, redirectUrl, scope);
                shareList.put(sharePlatformType.getName(), platform);
                keyInfoList.put(sharePlatformType, new KeyInfo(appKey, appSecret, redirectUrl, scope));
            }else{
                MLog.i(TAG, sharePlatformType.getEN_Name()+" platform not initialize");
                throw new NullPointerException(sharePlatformType.getEN_Name()+" platform not initialize");
            }

//        if (shareView==null){
//            shareView = new ShareView(mContext);
//        }
//        shareView.addItem();
        }
    }

    public AbsSharePlatform getSharePlatform(SharePlatformType sharePlatformType) {
        return getSharePlatform(sharePlatformType, true);
    }

    /**
     * @param sharePlatformType
     * @param initRes 是否初始化资源
     * @return
     */
    public AbsSharePlatform getSharePlatform(SharePlatformType sharePlatformType, boolean initRes) {
        AbsSharePlatform platform = null;
        if (shareList != null) {
            platform = shareList.get(sharePlatformType.getName());
            if (platform != null) {
                if (initRes) {
                    platform.initAllRes();
                    currentPlatformType = sharePlatformType;
                }
            }else{
                throw new NullPointerException("Has not been initialized configuration");
            }
        }
        return platform;
    }

    public KeyInfo getKeyInfo(SharePlatformType sharePlatformType) {
        KeyInfo keyInfo = null;
        if (keyInfoList != null) {
            keyInfo = keyInfoList.get(sharePlatformType);
        }
        return keyInfo;
    }

    public ShareListener getShareListener(SharePlatformType sharePlatformType) {
        ShareListener shareListener = null;
        AbsSharePlatform platform = getSharePlatform(sharePlatformType, false);
        if (platform != null) {
            shareListener = platform.getShareListener();
        }
        return shareListener;
    }

    public void onNewIntent(Intent intent) {
        AbsSharePlatform platform = getSharePlatform(currentPlatformType, false);
        if (platform != null) {
            platform.onNewIntent(intent);
        }
    }

    public static void show(){
        if (shareView!=null){

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AbsSharePlatform platform = getSharePlatform(currentPlatformType, false);
        if (platform != null) {
            platform.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void clearAll() {
        if (shareList != null) {
            shareList.clear();
            shareList = null;
        }
        if (keyInfoList != null) {
            keyInfoList.clear();
            keyInfoList = null;
        }
    }
}
