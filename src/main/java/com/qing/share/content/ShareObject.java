package com.qing.share.content;

/**
 * Created by zwq on 2015/11/05 15:51.<br/><br/>
 */
public abstract class ShareObject {

    private static final String TAG = ShareObject.class.getName();
    protected int objcetType;//0：文本，1：图片，2：其它多媒体

    protected String identify;
    protected String title;
    protected String description;//描述

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
}
