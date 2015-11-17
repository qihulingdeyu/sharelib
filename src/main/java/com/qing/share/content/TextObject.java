package com.qing.share.content;

/**
 * Created by zwq on 2015/11/04 12:05.<br/><br/>
 */
public class TextObject extends ShareObject {

    private static final String TAG = TextObject.class.getName();

    private String text;//文本

    public TextObject() {
        objcetType = 0;
    }

    @Override
    public int getObjcetType() {
        return objcetType;
    }

    public TextObject(String text, String description) {
        objcetType = 0;
        this.text = text;
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextObject{" +
                "description='" + description + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
