package com.qing.sharelib.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qing.widget.SelectorButton;
import com.qing.utils.UIUtil;

/**
 * Created by zwq on 2015/08/26 17:36.<br/><br/>
 */
public class ItemView extends LinearLayout {

    private static final String TAG = ItemView.class.getName();

    private Context mContext;
//    private ImageView icon;
    private SelectorButton icon;
    private TextView name;

    public ItemView(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutParams lParams = new LayoutParams(UIUtil.getRealPixel720(120), UIUtil.getRealPixel720(120));
        icon = new SelectorButton(mContext);
        addView(icon, lParams);

        lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.setMargins(0, 5, 0, 5);
        name = new TextView(mContext);
        name.setTextSize(12);
        addView(name, lParams);
    }

    public void setIcon(int normal, int pressed){
        if (icon != null){
            icon.setButtonImage(normal, pressed);
        }
    }

    public void setName(String _name){
        if (_name != null){
            name.setText(_name);
        }
    }
}
