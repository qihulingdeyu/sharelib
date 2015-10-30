package com.qing.share;

/**
 * Created by zwq on 2015/10/30 11:27.<br/><br/>
 */
public enum ShareType {

    QQ(1),
    QZONE(2),
    WEIXIN(3),
    WEIXIN_PYQ(4),
    SINA(5);

    private int mType = 1;
    ShareType(int type) {
        mType = type;
    }

    public String getName() {
        String name = "";
        switch (mType) {
            case 1:
                name = "QQ";
                break;
            case 2:
                name = "QZone";
                break;
            case 3:
                name = "微信";
                break;
            case 4:
                name = "朋友圈";
                break;
            case 5:
                name = "新浪";
                break;
            default:
                break;
        }
        return name;
    }
}
