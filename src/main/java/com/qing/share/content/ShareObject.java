package com.qing.share.content;

import android.text.TextUtils;

/**
 * Created by zwq on 2015/11/05 15:51.<br/><br/>
 */
public abstract class ShareObject {

    private static final String TAG = ShareObject.class.getName();
    public static final int TYPE_TEXT = 0;//0：文本
    public static final int TYPE_IMAGE = 1;//1：图片
    public static final int TYPE_MEDIA = 2;//2：其它多媒体

    private final String defaultActionUrl = "http://www.baidu.com/";//默认的点击跳转链接

    protected int objcetType;

    protected String identify;
    protected String title;
    protected String description;//描述
    /** 点击跳转链接 */
    protected String actionUrl;

    public ShareObject() {

    }

    /**
     * 0：文本，1：图片，2：其它多媒体
     * @return
     */
    public abstract int getObjcetType();

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getTitle() {
        if (TextUtils.isEmpty(title)) {
            title = "Title";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionUrl() {
        if (TextUtils.isEmpty(actionUrl) || !actionUrl.startsWith("http://")) {
            actionUrl = defaultActionUrl;
        }
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
