package com.qing.share;

/**
 * Created by zwq on 2015/10/30 17:25.<br/><br/>
 */
public interface ShareListener {

//    private static final String TAG = ShareListener.class.getName();
    void onStart();
    void onComplete();
    void onCancel();
    void onError(String msg);
}
