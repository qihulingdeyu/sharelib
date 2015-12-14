package com.qing.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.qing.log.MLog;
import com.qing.share.listener.ShareListener;
import com.qing.share.platforms.Share2Sina;
import com.qing.share.platforms.Share2Tencent;
import com.qing.share.platforms.Share2WeiXin;
import com.qing.share.ui.ShareView;
import com.qing.sharelib.R;
import com.qing.sharelib.ShareDialogActivity;

import java.util.HashMap;

/**
 * Created by zwq on 2015/08/26 17:07.<br/><br/>
 */
public class Share {

    private static final String TAG = Share.class.getName();
    private static Share instance;
    private Context mContext;
    private static ShareView shareView;
    private static HashMap<String, SharePlatform> shareList;
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
            SharePlatform platform = null;
            switch (sharePlatformType){
                case QQ:
                case QZONE:
                    platform = Share2Tencent.getInstance(mContext);
                    break;
                case WEIXIN:
                case WEIXIN_PYQ:
                    platform = Share2WeiXin.getInstance(mContext);
                    break;
                case SINA:
                    platform = Share2Sina.getInstance(mContext);
                    break;
                default:
                    throw new IllegalArgumentException("Currently does not support the platform");
            }
            if (platform != null) {
                platform.setShareConfig(appKey, appSecret, redirectUrl, scope);
                shareList.put(sharePlatformType.getName(), platform);
                keyInfoList.put(sharePlatformType, new KeyInfo(appKey, appSecret, redirectUrl, scope));
            }else{
                MLog.i(TAG, sharePlatformType.getEN_Name()+" platform not initialize");
                throw new NullPointerException(sharePlatformType.getEN_Name()+" platform not initialize");
            }

            addItemView(platform);
        }
    }

    public SharePlatform getSharePlatform(SharePlatformType sharePlatformType) {
        return getSharePlatform(sharePlatformType, true);
    }

    /**
     * @param sharePlatformType
     * @param initRes 是否初始化资源
     * @return
     */
    public SharePlatform getSharePlatform(SharePlatformType sharePlatformType, boolean initRes) {
        SharePlatform platform = null;
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
        SharePlatform platform = getSharePlatform(sharePlatformType, false);
        if (platform != null) {
            shareListener = platform.getShareListener();
        }
        return shareListener;
    }

    public void onNewIntent(Intent intent) {
        SharePlatform platform = getSharePlatform(currentPlatformType, false);
        if (platform != null) {
            platform.onNewIntent(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharePlatform platform = getSharePlatform(currentPlatformType, false);
        if (platform != null) {
            platform.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addDefaultView() {
        if (shareList != null) {
            for (int i = 0; i < shareList.size(); i++) {
                addItemView(shareList.get(i));
            }
        }
    }

    public void addItemView(SharePlatform platform) {
        if (platform == null) return;
        if (shareView == null){
            shareView = new ShareView(mContext);
        }
        switch (platform.getPlatformType()) {
            case QQ:
            case QZONE:
                shareView.addItem(SharePlatformType.QQ.getCN_Name(), R.mipmap.share_qq_normal, R.mipmap.share_qq_pressed);
                shareView.addItem(SharePlatformType.QZONE.getCN_Name(), R.mipmap.share_qqkj_normal, R.mipmap.share_qqkj_pressed);
                break;
            case WEIXIN:
            case WEIXIN_PYQ:
                shareView.addItem(SharePlatformType.WEIXIN.getCN_Name(), R.mipmap.share_weixin_normal, R.mipmap.share_weixin_pressed);
                shareView.addItem(SharePlatformType.WEIXIN_PYQ.getCN_Name(), R.mipmap.share_weixinpyq_normal, R.mipmap.share_weixinpyq_pressed);
                break;
            case SINA:
                shareView.addItem(SharePlatformType.SINA.getCN_Name(), R.mipmap.share_sina_normal, R.mipmap.share_sina_pressed);
                break;
            default:
                break;
        }
    }

    public View getShareView() {
        return shareView;
    }

    public void show(){
        if (shareView != null){
            ((Activity)mContext).startActivity(new Intent(mContext, ShareDialogActivity.class));
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
