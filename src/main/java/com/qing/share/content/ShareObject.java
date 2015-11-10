package com.qing.share.content;

/**
 * Created by zwq on 2015/11/05 15:51.<br/><br/>
 */
public abstract class ShareObject {

    private static final String TAG = ShareObject.class.getName();
    protected int objcetType;//0：文本，1：图片，2：其它多媒体

    public ShareObject() {

    }

    /**
     * 0：文本，1：图片，2：其它多媒体
     * @return
     */
    public abstract int getObjcetType();
}
