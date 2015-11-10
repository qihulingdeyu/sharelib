package com.qing.sharelib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by zwq on 2015/11/05 14:51.<br/><br/>
 */
public class ShareViewHolder {

    private static final String TAG = ShareViewHolder.class.getName();
    private View root;
    public RadioButton QQ, QZone, WeiXin, WeiXin_PYQ, Sina;
    public CheckBox text, image, webpage, music, video, voice;
    public EditText input_text;
    public Button clear_btn, send_btn, send_btn2, other_btn;

    public ShareViewHolder(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.share_demo, null);

        QQ = (RadioButton) root.findViewById(R.id.pt_qq);
        QZone = (RadioButton) root.findViewById(R.id.pt_qzone);
        WeiXin = (RadioButton) root.findViewById(R.id.pt_weixin);
        WeiXin_PYQ = (RadioButton) root.findViewById(R.id.pt_weixin_pyq);
        Sina = (RadioButton) root.findViewById(R.id.pt_sina);

        text = (CheckBox) root.findViewById(R.id.text);
        image = (CheckBox) root.findViewById(R.id.image);
        webpage = (CheckBox) root.findViewById(R.id.webpage);
        music = (CheckBox) root.findViewById(R.id.music);
        video = (CheckBox) root.findViewById(R.id.video);
        voice = (CheckBox) root.findViewById(R.id.voice);

        input_text = (EditText) root.findViewById(R.id.input_text);

        clear_btn = (Button) root.findViewById(R.id.clear_btn);
        send_btn = (Button) root.findViewById(R.id.send_btn);
        send_btn2 = (Button) root.findViewById(R.id.send_btn2);
        other_btn = (Button) root.findViewById(R.id.other_btn);

    }

    public View getView() {
        return root;
    }

    public void setOnClickListener(View.OnClickListener listener) {
//        QQ.setOnClickListener(listener);
//        QZone.setOnClickListener(listener);
//        WeiXin.setOnClickListener(listener);
//        WeiXin_PYQ.setOnClickListener(listener);
//        Sina.setOnClickListener(listener);

        clear_btn.setOnClickListener(listener);
        send_btn.setOnClickListener(listener);
        send_btn2.setOnClickListener(listener);
        other_btn.setOnClickListener(listener);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        QQ.setOnCheckedChangeListener(listener);
        QZone.setOnCheckedChangeListener(listener);
        WeiXin.setOnCheckedChangeListener(listener);
        WeiXin_PYQ.setOnCheckedChangeListener(listener);
        Sina.setOnCheckedChangeListener(listener);

        text.setOnCheckedChangeListener(listener);
        image.setOnCheckedChangeListener(listener);
        webpage.setOnCheckedChangeListener(listener);
        music.setOnCheckedChangeListener(listener);
        video.setOnCheckedChangeListener(listener);
        voice.setOnCheckedChangeListener(listener);
    }

    public void setChecked(boolean checked){
//        QQ.setChecked(checked);
//        QZone.setChecked(checked);
//        WeiXin.setChecked(checked);
//        WeiXin_PYQ.setChecked(checked);
//        Sina.setChecked(checked);

        text.setChecked(checked);
        image.setChecked(checked);
        webpage.setChecked(checked);
        music.setChecked(checked);
        video.setChecked(checked);
        voice.setChecked(checked);
    }

}
