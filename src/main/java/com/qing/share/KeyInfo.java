package com.qing.share;

/**
 * Created by zwq on 2015/11/05 10:22.<br/><br/>
 */
public class KeyInfo {

    private static final String TAG = KeyInfo.class.getName();
    private String appKey;
    private String appSecret;
    private String redirectUrl;
    private String scope;

    public KeyInfo() {

    }

    public KeyInfo(String appKey, String appSecret, String redirectUrl, String scope) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "KeyInfo{" +
                "appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
