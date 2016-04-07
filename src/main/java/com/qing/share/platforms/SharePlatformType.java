package com.qing.share.platforms;

/**
 * Created by zwq on 2015/10/30 11:27.<br/><br/>
 * 分享平台类型
 */
public enum SharePlatformType {

    QQ(1, "QQ好友", "QQ"),
    QZONE(2, "QQ空间", "QZone"),
    WEIXIN(3, "微信好友", "weixin"),
    WEIXIN_PYQ(4, "微信朋友圈", "weixin_pyq"),
    SINA(5, "新浪微博", "sina");

    private int mType;
    private String mCn;
    private String mEn;

    SharePlatformType(int type, String cn, String en) {
        mType = type;
        mCn = cn;
        mEn = en;
    }

    public int getType() {
        return mType;
    }

    public String getCnName() {
        return mCn;
    }

    public String getEnName() {
        return mEn;
    }

    public String getName() {
        return getCnName();
    }

}
