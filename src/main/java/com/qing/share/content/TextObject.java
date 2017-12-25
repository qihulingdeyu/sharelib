package com.qing.share.content;

/**
 * Created by zwq on 2015/11/04 12:05.<br/><br/>
 */
public class TextObject extends ShareObject {

    private String text;//文本

    public TextObject() {

    }

    @Override
    public int getObjectType() {
        return TYPE_TEXT;
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
