package com.qing.share;

/**
 * Created by zwq on 2015/11/02 11:48.<br/><br/>
 * 授权回调返回的信息
 */
public class OauthInfo {

    private static final String TAG = OauthInfo.class.getName();
    public String type = "";
    public String uid = "";
    public String accessToken = "";
    public String refreshToken = "";
    public String phoneNum = "";
    public String expiresIn = "";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "OauthInfo{" +
                "accessToken='" + accessToken + '\'' +
                ", uid='" + uid + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                '}';
    }
}
