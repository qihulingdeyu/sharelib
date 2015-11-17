package com.qing.share.content;

import android.graphics.Bitmap;

/**
 * Created by zwq on 2015/11/04 12:12.<br/><br/>
 */
public class ImageObject extends ShareObject {

    private static final String TAG = ImageObject.class.getName();
    private String path;//SD卡路径
    private String url;//网络地址
    private Bitmap bitmap;

    public ImageObject() {
        objcetType = 1;
    }

    @Override
    public int getObjcetType() {
        return objcetType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
