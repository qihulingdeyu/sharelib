package com.qing.share;

/**
 * Created by zwq on 2015/10/30 11:27.<br/><br/>
 * 分享平台类型
 */
public enum SharePlatformType {

    QQ(1),
    QZONE(2),
    WEIXIN(3),
    WEIXIN_PYQ(4),
    SINA(5);

    private int mType = 1;
    SharePlatformType(int type) {
        mType = type;
    }

    public String getName() {
        return getCN_Name();
    }

    public String getCN_Name() {
        String name = "";
        switch (mType) {
            case 1:
                name = "QQ好友";
                break;
            case 2:
                name = "QQ空间";
                break;
            case 3:
                name = "微信好友";
                break;
            case 4:
                name = "微信朋友圈";
                break;
            case 5:
                name = "新浪微博";
                break;
            default:
                break;
        }
        return name;
    }

    public String getEN_Name() {
        String name = "";
        switch (mType) {
            case 1:
                name = "QQ";
                break;
            case 2:
                name = "QZone";
                break;
            case 3:
                name = "weixin";
                break;
            case 4:
                name = "weixin_pyq";
                break;
            case 5:
                name = "sina";
                break;
            default:
                break;
        }
        return name;
    }
}
