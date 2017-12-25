package com.qing.share.content;

import android.graphics.Bitmap;

/**
 * Created by zwq on 2015/11/04 12:07.<br/><br/>
 */
public class MediaObject extends ShareObject {

    private Bitmap thumb;//缩略图
    private String thumbUrl;
    private String dataUrl;//数据源地址:sd卡目录、网络地址
    private String dataHdUrl;
    private int duration = 10;
    private String defaultText;//默认文本

    public MediaObject() {

    }

    @Override
    public int getObjectType() {
        return TYPE_MEDIA;
    }

    public String getDataHdUrl() {
        return dataHdUrl;
    }

    public void setDataHdUrl(String dataHdUrl) {
        this.dataHdUrl = dataHdUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @Override
    public String toString() {
        return "MediaObject{" +
                "actionUrl='" + actionUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumb=" + thumb +
                ", thumbUrl=" + thumbUrl +
                ", dataUrl='" + dataUrl + '\'' +
                ", dataHdUrl='" + dataHdUrl + '\'' +
                ", duration=" + duration +
                ", defaultText='" + defaultText + '\'' +
                '}';
    }
}
