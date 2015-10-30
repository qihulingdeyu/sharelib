package com.qing.share.platforms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.qing.share.AbsSharePlatform;
import com.qing.share.AccessTokenKeeper;
import com.qing.share.ShareListener;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by zwq on 2015/10/30 11:49.<br/><br/>
 */
public class Share2Sina extends AbsSharePlatform {

    private static final String TAG = Share2Sina.class.getName();

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private final int SHARE_CLIENT = 1;
    private final int SHARE_ALL_IN_ONE = 2;
    private int mShareType = SHARE_CLIENT;

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;
    private WeiboMultiMessage weiboMultiMessage;

    private static Share2Sina instance;

    private Share2Sina(Context context) {
        super(context);
        platform_prefix = "sina_";
        getShareConfig();
//        init();
    }

    public static Share2Sina getInstance(Context context) {
        if (instance == null) {
            synchronized (Share2WeiXin.class) {
                if (instance == null) {
                    instance = new Share2Sina(context);
                }
            }
        }
        return instance;
    }

    public void init(String appKey, String redirectUrl, String scope) {
        if (mSsoHandler == null) {
            if (mAuthInfo == null){
                mAuthInfo = new AuthInfo(mContext, appKey, redirectUrl, scope);
            }
            mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
        }
    }

    @Override
    public void setShareListener(ShareListener shareListener) {
        super.setShareListener(shareListener);
    }

    @Override
    public boolean isAppInstalled() {
        return mSsoHandler.isWeiboAppInstalled();
    }

    @Override
    public void authorize(int type, Context context, String appKey, String redirectUrl, String scope) {
        mContext = context;
        init(appKey, redirectUrl, scope);

        switch (type) {
            case 0:
                mSsoHandler.authorize(new AuthorListener());
                break;
            case 1:
                mSsoHandler.authorizeWeb(new AuthorListener());
                break;
            case 2:
//                AidInfo aidInfo = AidTask4Plug.getInstance(WBAuthActivity.this, "123456").getAid4PlugSync( "123456", "pak", "hash");
                mSsoHandler.authorizeClientSso(new AuthorListener());
                break;
            default:
                break;
        }
    }

    @Override
    public void register() {

    }

    /**
     * 手机短信授权
     * title 短信注册页面title，可选，不传时默认为""验证码登录""。此处WeiboAuthListener 对象 listener
     * 可以是和sso 同一个 listener   回调对象 也可以是不同的。开发者根据需要选择
     */
    @Override
    public void registerOrLoginByMobile() {
        mSsoHandler.registerOrLoginByMobile("验证码登录", new AuthorListener());
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {
        AccessTokenKeeper.clear(mContext);
        mAccessToken = new Oauth2AccessToken();
        updateTokenView(false);

//        mAccessToken = AccessTokenKeeper.readAccessToken(WBLoginLogoutActivity.this);
//        com.sina.weibo.sdk.openapi.LogoutAPI api = new LogoutAPI(mContext, getAppKey(), mAccessToken).logout(mLogoutListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
//        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                new java.util.Date(mAccessToken.getExpiresTime()));
//        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
//
//        String message = String.format(format, mAccessToken.getToken(), date);
//        if (hasExisted) {
//            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
//        }
//        mTokenText.setText(message);
    }




    private void initWeiboMultiMessage() {
        if (weiboMultiMessage == null) {
            weiboMultiMessage = new WeiboMultiMessage();
        }
    }

    /**
     * 文本消息对象。
     *
     * @param text
     */
    @Override
    public void shareText(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;

        initWeiboMultiMessage();
        weiboMultiMessage.textObject = textObject;
    }

    /**
     * 图片消息对象。
     * 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     *
     * @param bitmap
     */
    @Override
    public void shareImage(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);

        initWeiboMultiMessage();
        weiboMultiMessage.imageObject = imageObject;
    }

    /**
     * 多媒体（网页）消息对象。
     *
     * @param title
     * @param description
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param defaultText Webpage 默认文案
     */
    @Override
    public void shareWebPage(String title, String description, Bitmap thumb, String actionUrl, String defaultText) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;

        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(thumb);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = defaultText;

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = mediaObject;
    }

    /**
     * 多媒体（音乐）消息对象。
     *
     * @param title
     * @param description
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param dataUrl
     * @param dataHdUrl
     * @param duration
     * @param defaultText
     */
    @Override
    public void shareMusic(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {
        if (duration <= 0) {
            duration = 10;
        }
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = title;
        musicObject.description = description;

        musicObject.setThumbImage(thumb);
        musicObject.actionUrl = actionUrl;
        musicObject.dataUrl = dataUrl;
        musicObject.dataHdUrl = dataHdUrl;
        musicObject.duration = duration;
        musicObject.defaultText = defaultText;

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = musicObject;
    }

    /**
     * 多媒体（视频）消息对象。
     *
     * @param title
     * @param description
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param dataUrl
     * @param dataHdUrl
     * @param duration
     * @param defaultText
     */
    @Override
    public void shareVideo(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {
        if (duration <= 0) {
            duration = 10;
        }
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = title;
        videoObject.description = description;

        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.JPEG, 85, os);
            Log.d(TAG, "thumb size:" + os.toByteArray().length);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "put thumb failed");
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        videoObject.setThumbImage(thumb);
        videoObject.actionUrl = actionUrl;
        videoObject.dataUrl = dataUrl;
        videoObject.dataHdUrl = dataHdUrl;
        videoObject.duration = duration;
        videoObject.defaultText = defaultText;

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = videoObject;
    }

    /**
     * 多媒体（音频）消息对象。
     *
     * @param title
     * @param description
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param dataUrl
     * @param dataHdUrl
     * @param duration
     * @param defaultText
     */
    @Override
    public void shareVoice(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText) {
        if (duration <= 0) {
            duration = 10;
        }
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = title;
        voiceObject.description = description;

        voiceObject.setThumbImage(thumb);
        voiceObject.actionUrl = actionUrl;
        voiceObject.dataUrl = dataUrl;
        voiceObject.dataHdUrl = dataHdUrl;
        voiceObject.duration = duration;
        voiceObject.defaultText = defaultText;

        initWeiboMultiMessage();
        weiboMultiMessage.mediaObject = voiceObject;
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    @Override
    public boolean sendSingleMessage() {
        if (weiboMultiMessage == null) return false;

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (weiboMultiMessage.textObject != null) {
            weiboMessage.mediaObject = weiboMultiMessage.textObject;

        } else if (weiboMultiMessage.imageObject != null) {
            weiboMessage.mediaObject = weiboMultiMessage.imageObject;

        } else if (weiboMultiMessage.mediaObject != null) {
            weiboMessage.mediaObject = weiboMultiMessage.mediaObject;
        } else {
            weiboMultiMessage = null;
            weiboMessage = null;
            return false;
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        return mWeiboShareAPI.sendRequest((Activity) mContext, request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    @Override
    public boolean sendMultiMessage() {
        boolean result = false;
        if (weiboMultiMessage == null) return result;
        // 1. 初始化微博的分享消息

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMultiMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            result = mWeiboShareAPI.sendRequest((Activity) mContext, request);

        } else if (mShareType == SHARE_ALL_IN_ONE) {
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mContext);
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            result = mWeiboShareAPI.sendRequest((Activity) mContext, request, mAuthInfo, token, new AuthorListener());
        }
        return result;
    }

    class AuthorListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle bundle) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = bundle.getString("code");
//                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
            }
            if (mShareListener != null) {
                mShareListener.onComplete();
            }

//                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                    AccessTokenKeeper.writeAccessToken(mContext, newToken);
//                    Toast.makeText(mContext, "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (mShareListener != null) {
                mShareListener.onError(e.getMessage());
            }
        }

        @Override
        public void onCancel() {
            if (mShareListener != null) {
                mShareListener.onCancel();
            }
        }
    }

}
