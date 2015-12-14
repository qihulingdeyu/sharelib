package com.qing.share.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.qing.utils.UIUtils;

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

        lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.setMargins(UIUtils.getRealPixel720(50), UIUtils.getRealPixel720(20), 0, 0);
    }

    public void addItem(String name, int normal, int pressed){
        ItemView itemView = new ItemView(mContext);
        itemView.setName(name);
        itemView.setIcon(normal, pressed);
        addView(itemView, lParams);
    }
}
