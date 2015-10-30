package com.qing.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

/**
 * Created by zwq on 2015/10/30 11:52.<br/><br/>
 */
public class AbsSharePlatform implements SharePlatform {

    private static final String TAG = AbsSharePlatform.class.getName();

    /** 分配给每个第三方应用的App key。用于鉴权身份，显示来源等功能。 */
    protected String consumerKey = null;
    /** 生成请求Request Token的secret，与Consumer key一起分配。 */
    protected String consumerKeySecret = null;
    /** 服务器根据App key和时间，Callback_url等哈希出的Token值，用于获取OAuth Verifier。 */
    protected String oAuthToken = null;
    /** 与OAuth Token一起使用，用于获取OAuth Verifier。 */
    protected String oAuthTokenSecret = null;
    /** 通过OAuth页面返回的verifier，用于最终获取Access Token。 */
    protected String oAuthVerifier = null;
    /** 表示用户身份的Token，用于微博API的调用。 */
    protected String accessToken = null;
    /** 授权回调页地址 */
    protected String redirectUrl = null;
    /** 应用申请的 高级权限 */
    protected String scope = null;

    protected Context mContext;
    protected ShareListener mShareListener;
    protected String socialShareConfig = "share_config";
    protected String platform_prefix = "";

    protected AbsSharePlatform(Context context) {
        mContext = context;
    }

    public void getShareConfig() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(socialShareConfig, Context.MODE_PRIVATE);
        sharedPreferences.getString(platform_prefix+"", "");
    }

    public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }


    @Override
    public String getAppKey() {
        return consumerKey;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public boolean isAppInstalled() {
        return false;
    }

    @Override
    public void authorize() {

    }

    @Override
    public void authorize(int type, Context context, String appKey, String redirectUrl, String scope) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void shareText(String text) {

    }

    @Override
    public void shareImage(Bitmap bitmap) {

    }

    @Override
    public void shareWebPage(String title, String description, Bitmap thumb, String actionUrl, String defaultText) {

    }

    @Override
    public void shareMusic(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {

    }

    @Override
    public void shareVideo(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {

    }

    @Override
    public void shareVoice(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {

    }

    @Override
    public boolean sendSingleMessage() {
        return false;
    }

    @Override
    public boolean sendMultiMessage() {
        return false;
    }
}
