package com.qing.share.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zwq on 2015/08/26 17:36.<br/><br/>
 */
public class ItemView extends LinearLayout {

    private static final String TAG = ItemView.class.getName();

    private Context mContext;
    private ImageView icon;
    private TextView name;

    public ItemView(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        icon = new ImageView(mContext);
        addView(icon, lParams);

        lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.setMargins(0, 5, 0, 5);
        name = new TextView(mContext);
        name.setTextSize(16);
        addView(name, lParams);
    }

    public void setIcon(int normal, int pressed){
        if (normal!=-1){
            if (pressed==-1){
                pressed = normal;
            }
            icon.setImageResource(normal);
        }
    }

    public void setName(String _name){
        if (_name!=null){
            name.setText(_name);
        }
    }
}
