package com.qing.sharelib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qing.log.MLog;
import com.qing.share.KeyInfo;
import com.qing.share.Share;
import com.qing.share.SharePlatformType;
import com.qing.share.listener.ShareListener;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by zwq on 2015/11/05 09:30.<br/><br/>
 * 在 onCreate 方法中已经调用了 initWXHandler 方法初始化WXAPIFactory
 */
public abstract class WXEntryCallbackActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryCallbackActivity.class.getName();
    protected Context mContext;
    protected IWXAPI api;
    protected boolean initInParent;
    private Share mShare;
    protected ShareListener mShareListener;

    protected abstract void onRequest(BaseReq baseReq);

    protected abstract void onResponse(BaseResp baseResp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        MLog.i(TAG, "---------onCreate----------");

        initWXHandler();
    }

    /**
     * 初始化 WXAPIFactory
     */
    protected final void initWXHandler() {
        mShare = Share.getInstance(mContext);
        KeyInfo keyInfo = mShare.getKeyInfo(SharePlatformType.WEIXIN);
        if (keyInfo != null) {
//            MLog.i(TAG, "---------initWXHandler----------key:"+keyInfo.getAppKey());
            api = WXAPIFactory.createWXAPI(mContext, keyInfo.getAppKey());
            api.registerApp(keyInfo.getAppKey());
            api.handleIntent(getIntent(), this);
            initInParent = true;
        }else{
            throw new NullPointerException("IWXAPI not initalize in WXEntryCallbackActivity");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        MLog.i(TAG, "---------onNewIntent----------");
        if (initInParent && api != null) {
            setIntent(intent);
            api.handleIntent(intent, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        MLog.i(TAG, "---------onActivityResult----------");
    }

    @Override
    public final void onReq(BaseReq baseReq) {
//        MLog.i(TAG, "---------onRequest----------");
        onRequest(baseReq);
        finish();
    }

    @Override
    public final void onResp(BaseResp baseResp) {
//        MLog.i(TAG, "--onResponse--type:"+baseResp.getType()+", oId:"+baseResp.openId+", transaction:"+baseResp.transaction);
        mShareListener = mShare.getShareListener(SharePlatformType.WEIXIN);
        if (mShareListener != null) {
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    mShareListener.onShareSuccess(mShare.getSharePlatform(SharePlatformType.WEIXIN, false).getPlatformType(), null);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    mShareListener.onShareCancel();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    mShareListener.onShareFail("msg:"+baseResp.errStr);
                    break;
                default:
                    mShareListener.onShareFail("code:"+baseResp.errCode+", msg:"+baseResp.errStr);
                    break;
            }
        }

        onResponse(baseResp);
        finish();
    }
}
