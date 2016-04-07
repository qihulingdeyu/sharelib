package com.qing.share.platforms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.qing.share.OauthInfo;
import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.TextObject;
import com.qing.share.listener.OauthListener;
import com.qing.share.listener.ShareListener;

/**
 * Created by zwq on 2015/10/30 11:52.<br/><br/>
 */
public abstract class SharePlatform {

    private static final String TAG = SharePlatform.class.getName();

    /** 分配给每个第三方应用的App key。用于鉴权身份，显示来源等功能。 */
    protected String appKey = null;
    /** 生成请求Request Token的secret，与Consumer key一起分配。 */
    protected String appSecret = null;
    /** 授权回调页地址 */
    protected String redirectUrl = null;
    /** 应用申请的 高级权限 */
    protected String scope = null;

    protected int authorizeType;
    /** 是否是授权操作 */
    protected boolean isAuthorize;

//    /** 服务器根据App key和时间，Callback_url等哈希出的Token值，用于获取OAuth Verifier。 */
//    protected String oAuthToken = null;
//    /** 与OAuth Token一起使用，用于获取OAuth Verifier。 */
//    protected String oAuthTokenSecret = null;
//    /** 通过OAuth页面返回的verifier，用于最终获取Access Token。 */
//    protected String oAuthVerifier = null;
//    /** 表示用户身份的Token，用于微博API的调用。 */
//    protected String accessToken = null;

    protected Context mContext;
    protected ShareListener mShareListener;
    protected OauthListener mOauthListener;
    protected String socialShareConfig = "qing_share_config";//轻分享
    protected String platform_prefix = "";
    protected OauthInfo oauthInfo;
    protected int sendMessageToWhere;

    /** 缩略图大小 */
    protected final int THUMB_MAX_SIZE = 130;
    /** 缩略图数组长度 (32K大小) */
    protected final int THUMB_MAX_ARRAY_LENGTH = 32768;

    protected SharePlatform() {
    }

    protected SharePlatform(Context context) {
        mContext = context;
    }

    protected Activity getActivity() {
        return (Activity)mContext;
    }

    protected Intent getIntent() {
        return getActivity().getIntent();
    }

    protected String getAppName() {
        if (mContext != null) {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES).get(0);
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        }
        return null;
    }

    public void setShareConfig(String appKey, String appSecret, String redirectUrl, String scope) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }

    public void setOauthInfo(OauthInfo oauthInfo) {
        if (oauthInfo == null) return;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(socialShareConfig, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(platform_prefix + "uid", oauthInfo.getUid());
        editor.putString(platform_prefix + "accessToken", oauthInfo.getAccessToken());
        editor.putString(platform_prefix + "refreshToken", oauthInfo.getRefreshToken());
        editor.putString(platform_prefix + "phoneNum", oauthInfo.getPhoneNum());
        editor.putString(platform_prefix + "expiresIn", oauthInfo.getExpiresIn());
        editor.commit();
        editor = null;
    }

    public OauthInfo getOauthInfo() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(socialShareConfig, Context.MODE_PRIVATE);
        OauthInfo oauthInfo = new OauthInfo();
        oauthInfo.setUid(sharedPreferences.getString(platform_prefix + "uid", ""));
        oauthInfo.setAccessToken(sharedPreferences.getString(platform_prefix + "accessToken", ""));
        oauthInfo.setRefreshToken(sharedPreferences.getString(platform_prefix + "refreshToken", ""));
        oauthInfo.setPhoneNum(sharedPreferences.getString(platform_prefix + "phoneNum", ""));
        oauthInfo.setExpiresIn(sharedPreferences.getString(platform_prefix + "expiresIn", ""));
        return oauthInfo;
    }

    public abstract void initAllRes();

    public abstract SharePlatformType getPlatformType();

    public void setOauthListener(OauthListener oauthListener) {
        mOauthListener = oauthListener;
    }

    public OauthListener getOauthListener() {
        return mOauthListener;
    }

    public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }

    public ShareListener getShareListener() {
        return mShareListener;
    }

    public final void unsupported() {
        throw new IllegalArgumentException("This is not supported by the operating in the current platform");
    }

    /**
     * 将appKey注册到客户端，以便调用客户端
     */
    public abstract void registerToClient();

    public void unregisterClient() {

    }

    /**
     * 是否安装了客户端
     * @return
     */
    public abstract boolean isAppInstalled();

    /**
     * 设置授权类型
     * @param type
     */
    public void setAuthorizeType(int type) {
        authorizeType = type;
    }
    /**
     * 授权
     */
    public void authorize() {
        authorize(authorizeType);
    }

    /**
     * 授权
     *
     * @param type        0:ALL IN ONE, 如果手机安装了客户端则使用客户端授权,没有则进行网页授权, 1:仅Web, 2:仅客户端
     */
    public abstract void authorize(int type);

    /**
     * 注册
     */
    public abstract void register();

    /**
     * 手机注册、登录
     */
    public abstract void registerOrLoginByMobile();

    /**
     * 登录
     */
    public abstract void login();

    /**
     * 退出登录
     */
    public abstract void logout();

    /**
     *   QQ：（0：QQ，1：QZone）
     * 微信：（0：好友，1：朋友圈）
     * @param where
     */
    public abstract void setSendMessageToWhere(int where);

    public int getSendMessageToWhere() {
        return sendMessageToWhere;
    }

    /**
     * 分享应用
     * @param textObject
     */
    public abstract void shareApp(TextObject textObject);

    /**
     * 文本消息对象。
     *
     * @param textObject
     */
    public abstract void shareText(TextObject textObject);

    /**
     * 图片消息对象。
     * 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     *
     * @param imageObject
     */
    public abstract void shareImage(ImageObject imageObject);

    /**
     * 多媒体（网页）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    public abstract void shareWebPage(MediaObject mediaObject);

    /**
     * 多媒体（音乐）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    public abstract void shareMusic(MediaObject mediaObject);

    /**
     * 多媒体（视频）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    public abstract void shareVideo(MediaObject mediaObject);

    /**
     * 多媒体（音频）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    public abstract void shareVoice(MediaObject mediaObject);

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 只支持分享单条消息，即文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    public abstract void sendSingleMessage();

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 支持同时分享多条消息，同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    public abstract void sendMultiMessage();

//    public void sendMessage() {
//
//    }
//
//    public void sendMessage(int type) {
//
//    }

    public abstract void onNewIntent(Intent intent);

    /**
     * SSO 授权回调, 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 清除所有资源
     */
    public abstract void clearAll();

}
