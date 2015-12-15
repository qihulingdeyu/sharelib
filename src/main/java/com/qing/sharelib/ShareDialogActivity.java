package com.qing.sharelib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qing.log.MLog;
import com.qing.share.Share;

public class ShareDialogActivity extends Activity implements View.OnClickListener{

    private static final String TAG = ShareDialogActivity.class.getName();
    private LinearLayout share_empty;
    private HorizontalScrollView share_content;
    private Button share_cancel;
    private Share mShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_dialog);

        share_empty = (LinearLayout) findViewById(R.id.share_empty);
        share_content = (HorizontalScrollView) findViewById(R.id.share_content);
        share_cancel = (Button) findViewById(R.id.share_cancel);
        share_empty.setOnClickListener(this);
        share_cancel.setOnClickListener(this);

        mShare = Share.getInstance(this);
        mShare.initSharePlatform(this);
        mShare.addDefaultView();

        share_content.removeAllViewsInLayout();
        share_content.addView(mShare.getShareView());
    }

    @Override
    public void onNewIntent(Intent intent) {
//        MLog.i(TAG, "---onNewIntent--dialog-");
        if (mShare != null) {
            mShare.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        MLog.i(TAG, "---onActivityResult--dialog-");
        if (mShare != null) {
            mShare.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == share_empty || v == share_cancel) {
            onBackPressed();
        }
    }

    @Override
    public void finish() {
//        MLog.i(TAG, "---finish--dialog-");
        super.finish();
        overridePendingTransition(R.anim.anim_close_enter, R.anim.anim_close_exit);

        if (share_content != null) {
            share_content.removeAllViewsInLayout();
        }
        if (mShare != null) {
            mShare.removeAllViews();
        }
    }
}
