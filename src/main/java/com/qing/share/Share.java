package com.qing.share;

import android.content.Context;

import com.qing.share.ui.ShareView;

/**
 * Created by zwq on 2015/08/26 17:07.<br/><br/>
 */
public class Share {

    private static final String TAG = Share.class.getName();
    private static Share instance;
    private Context mContext;
    private static ShareView shareView;

    public static Share getInstance(Context context){
        if(instance==null){
            synchronized (instance){
                if (instance==null){
                    instance = new Share(context);
                }
            }
        }
        return instance;
    }

    private Share(){}
    private Share(Context context){
        mContext = context;
    }

    public void addShare(ShareType shareType){
        switch (shareType){
            case QQ:
                break;
            case WEIXIN:
                break;
            case WEIXIN_PYQ:
                break;
            case SINA:
                break;
            default:
                break;
        }
        if (shareView==null){
            shareView = new ShareView(mContext);
        }
//        shareView.addItem();

    }

    public static void show(){
        if (shareView!=null){

        }
    }

}
