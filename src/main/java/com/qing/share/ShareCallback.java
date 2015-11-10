package com.qing.share;

import com.qing.callback.AbstractTaskCallback;

/**
 * Created by zwq on 2015/11/06 17:56.<br/><br/>
 */
public abstract class ShareCallback extends AbstractTaskCallback {

    private static final String TAG = ShareCallback.class.getName();

    public abstract void onFinish();

    @Override
    public void dispatchResult(int state, Object... data) {

    }
}
