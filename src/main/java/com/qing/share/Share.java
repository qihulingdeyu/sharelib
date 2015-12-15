package com.qing.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.qing.log.MLog;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.ShareObject;
import com.qing.share.content.TextObject;
import com.qing.share.listener.OauthListener;
import com.qing.share.listener.ShareListener;
import com.qing.share.platforms.Share2Sina;
import com.qing.share.platforms.Share2Tencent;
import com.qing.share.platforms.Share2WeiXin;
import com.qing.share.ui.ShareView;
import com.qing.sharelib.R;
import com.qing.sharelib.ShareDialogActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zwq on 2015/08/26 17:07.<br/><br/>
 *
 * <activity android:name="com.qing.sharelib.ShareDialogActivity"
 * android:theme="@style/share_dialog" />
 */
public class Share {

    private static final String TAG = Share.class.getName();
    private static Share instance;
    private Context mContext;
    private Activity mActivity;

    //LinkedHashMap是按插入顺序存储的
    private static LinkedHashMap<String, SharePlatform> shareList;
    private static LinkedHashMap<SharePlatformType, KeyInfo> keyInfoList;
    private SharePlatformType currentPlatformType;

    private static ShareView shareView;
    private ItemViewClickListener mItemViewClickListener;
    private ShareListener mShareListener;
    private OauthListener mOauthListener;
    private ShareObject mShareObject;

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
        mActivity = (Activity) context;
        shareList = new LinkedHashMap<String, SharePlatform>();
        keyInfoList = new LinkedHashMap<SharePlatformType, KeyInfo>();
        MLog.setDebugMode(true);
    }

    public void addShare(SharePlatformType sharePlatformType, String appKey, String appSecret, String redirectUrl, String scope){
        if (!keyInfoList.containsKey(sharePlatformType)) {
            keyInfoList.put(sharePlatformType, new KeyInfo(appKey, appSecret, redirectUrl, scope));
        }
    }

    public void initSharePlatform(Context context) {
        if (context != null) {
            mContext = context;
        }
        for (Map.Entry<SharePlatformType, KeyInfo> entry : keyInfoList.entrySet()) {
            SharePlatformType sharePlatformType = entry.getKey();
            KeyInfo keyInfo = entry.getValue();
            initShare(sharePlatformType, keyInfo.getAppKey(), keyInfo.getAppSecret(), keyInfo.getRedirectUrl(), keyInfo.getScope());
        }
    }

    private void initShare(SharePlatformType sharePlatformType, String appKey, String appSecret, String redirectUrl, String scope){
        if (shareList == null) {
            shareList = new LinkedHashMap<String, SharePlatform>();
        }
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
            }else{
                MLog.i(TAG, sharePlatformType.getEN_Name() + " platform not initialize");
                throw new NullPointerException(sharePlatformType.getEN_Name()+" platform not initialize");
            }
//            addItemView(platform);
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

    public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }

    public void setOauthListener(OauthListener oauthListener) {
        mOauthListener = oauthListener;
    }

    public void setShareObject(ShareObject shareObject) {
        mShareObject = shareObject;
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
            for (Map.Entry<String, SharePlatform> entry : shareList.entrySet()) {
//                String sharePlatformName = entry.getKey();
                SharePlatform sharePlatform = entry.getValue();
                addItemView(sharePlatform);
            }
        }
    }

    private void addItemView(SharePlatform platform) {
        if (platform == null) return;
        if (shareView == null){
            shareView = new ShareView(mContext);
        }
        if (mItemViewClickListener == null) {
            mItemViewClickListener = new ItemViewClickListener();
        }
        switch (platform.getPlatformType()) {
            case QQ:
            case QZONE:
                shareView.addItem(SharePlatformType.QQ.getType(), SharePlatformType.QQ.getCN_Name(), R.mipmap.share_qq_normal, R.mipmap.share_qq_pressed, mItemViewClickListener);
                shareView.addItem(SharePlatformType.QZONE.getType(), SharePlatformType.QZONE.getCN_Name(), R.mipmap.share_qqkj_normal, R.mipmap.share_qqkj_pressed, mItemViewClickListener);
                break;
            case WEIXIN:
            case WEIXIN_PYQ:
                shareView.addItem(SharePlatformType.WEIXIN.getType(), SharePlatformType.WEIXIN.getCN_Name(), R.mipmap.share_weixin_normal, R.mipmap.share_weixin_pressed, mItemViewClickListener);
                shareView.addItem(SharePlatformType.WEIXIN_PYQ.getType(), SharePlatformType.WEIXIN_PYQ.getCN_Name(), R.mipmap.share_weixinpyq_normal, R.mipmap.share_weixinpyq_pressed, mItemViewClickListener);
                break;
            case SINA:
                shareView.addItem(SharePlatformType.SINA.getType(), SharePlatformType.SINA.getCN_Name(), R.mipmap.share_sina_normal, R.mipmap.share_sina_pressed, mItemViewClickListener);
                break;
            default:
                break;
        }
    }

    private void addItemView(SharePlatformType sharePlatformType, View itemView) {
        if (itemView == null) return;
        if (shareView == null){
            shareView = new ShareView(mContext);
        }
        if (mItemViewClickListener == null) {
            mItemViewClickListener = new ItemViewClickListener();
        }
        itemView.setId(sharePlatformType.getType());
        itemView.setOnClickListener(mItemViewClickListener);
        shareView.addView(itemView);
    }

    public View getShareView() {
        return shareView;
    }

    public void show() {
        if (mActivity != null) {
            mActivity.startActivity(new Intent(mActivity, ShareDialogActivity.class));
        }
    }

    class ItemViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            MLog.i(TAG, "onClick  id:"+v.getId());
            SharePlatform sharePlatform = null;
            if (v.getId() == SharePlatformType.QQ.getType()){
                sharePlatform = getSharePlatform(SharePlatformType.QQ);
                sharePlatform.setSendMessageToWhere(0);

            } else if (v.getId() == SharePlatformType.QZONE.getType()){
                sharePlatform = getSharePlatform(SharePlatformType.QQ);
                sharePlatform.setSendMessageToWhere(1);

            } else if (v.getId() == SharePlatformType.WEIXIN.getType()){
                sharePlatform = getSharePlatform(SharePlatformType.WEIXIN);
                sharePlatform.setSendMessageToWhere(0);

            } else if (v.getId() == SharePlatformType.WEIXIN_PYQ.getType()){
                sharePlatform = getSharePlatform(SharePlatformType.WEIXIN);
                sharePlatform.setSendMessageToWhere(1);

            } else if (v.getId() == SharePlatformType.SINA.getType()){
                sharePlatform = getSharePlatform(SharePlatformType.SINA);
            }
            if (sharePlatform == null) {
                MLog.i(TAG, "SharePlatform is null!");
                if (mShareListener != null) {
                    mShareListener.onShareFail("SharePlatform is null!");
                }
            }else{
                if (mShareObject == null) {
                    MLog.i(TAG, "ShareObject is null!");
                    if (mShareListener != null) {
                        mShareListener.onShareFail("ShareObject is null!");
                    }
                }else{
//                MLog.i(TAG, "onClick  sharePlatform type:" + sharePlatform.getPlatformType().getName()+", ObjcetType:"+mShareObject.getObjcetType());
                    switch (mShareObject.getObjcetType()) {
                        case ShareObject.TYPE_TEXT:
                            sharePlatform.shareText((TextObject) mShareObject);
                            break;
                        case ShareObject.TYPE_IMAGE:
                            sharePlatform.shareImage((ImageObject) mShareObject);
                            break;
                        case ShareObject.TYPE_MEDIA:
                            sharePlatform.shareWebPage((MediaObject) mShareObject);
                            break;
                        default:
                            break;
                    }
                    sharePlatform.setShareListener(mShareListener);
                    sharePlatform.setOauthListener(mOauthListener);
                    sharePlatform.sendSingleMessage();
                }
            }

        }
    }

    public void removeAllViews() {
        if (shareView != null) {
            shareView.removeAllViewsInLayout();
            shareView = null;
        }
        if (shareList != null) {
            shareList.clear();
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
