package com.qing.sharelib.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.qing.utils.UIUtil;

/**
 * Created by zwq on 2015/08/26 17:50.<br/><br/>
 */
public class ShareView extends LinearLayout {

    private static final String TAG = ShareView.class.getName();

    private Context mContext;
    private LayoutParams lParams;
    public ShareView(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, 0, UIUtil.getRealPixel720(50), 0);

        lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.setMargins(UIUtil.getRealPixel720(50), UIUtil.getRealPixel720(20), 0, 0);
    }

    public void addItem(String name, int normal, int pressed){
        ItemView itemView = new ItemView(mContext);
        itemView.setName(name);
        itemView.setIcon(normal, pressed);
        addView(itemView, lParams);
    }

    public View addItem(int id, String name, int normal, int pressed, View.OnClickListener clickListener){
        ItemView itemView = new ItemView(mContext);
        itemView.setId(id);
        itemView.setName(name);
        itemView.setIcon(normal, pressed);
        itemView.setOnClickListener(clickListener);
        addView(itemView, lParams);
        return itemView;
    }
}
