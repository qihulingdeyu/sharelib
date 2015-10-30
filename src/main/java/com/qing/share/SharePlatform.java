package com.qing.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by zwq on 2015/10/30 11:31.<br/><br/>
 * 所有分享平台都有实现此接口
 */
public interface SharePlatform {

    /**
     * 分配给每个第三方应用的 app key
     */
    String getAppKey();

    /**
     * 第三方应用授权回调页面
     */
    String getRedirectUrl();
//    /** 授权功能，（保留 新浪有些权限有限制）*/
//    String getScope();

    /**
     * 表示用户身份的 token
     */
    String getAccessToken();

    /**
     * 是否安装了客户端
     * @return
     */
    boolean isAppInstalled();

    /**
     * 授权
     */
    void authorize();

    /**
     * 授权
     *
     * @param type        0:ALL IN ONE, 如果手机安装了客户端则使用客户端授权,没有则进行网页授权, 1:仅Web, 2:仅客户端
     * @param context
     * @param appKey
     * @param redirectUrl
     * @param scope       可以为null
     */
    void authorize(int type, Context context, String appKey, String redirectUrl, String scope);

    /**
     * 注册
     */
    void register();

    /**
     * 手机注册、登录
     */
    void registerOrLoginByMobile();

    /**
     * 登录
     */
    void login();

    /**
     * 退出登录
     */
    void logout();

    /**
     * SSO 授权回调, 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 文本消息对象。
     *
     * @param text
     */
    void shareText(String text);

    /**
     * 图片消息对象。
     * 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     *
     * @param bitmap
     */
    void shareImage(Bitmap bitmap);

    /**
     * 多媒体（网页）消息对象。
     *
     * @param title
     * @param description
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param defaultText Webpage 默认文案
     */
    void shareWebPage(String title, String description, Bitmap thumb, String actionUrl, String defaultText);

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
    void shareMusic(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText);

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
    void shareVideo(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText);

    /**
     * 多媒体（音频）消息对象。
     *
     * @param title 标题
     * @param description 描述
     * @param thumb       设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     * @param actionUrl
     * @param dataUrl
     * @param dataHdUrl
     * @param duration
     * @param defaultText 默认文本
     */
    void shareVoice(String title, String description, Bitmap thumb, String actionUrl, String dataUrl, String dataHdUrl, int duration, String defaultText);

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 只支持分享单条消息，即文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    boolean sendSingleMessage();

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 支持同时分享多条消息，同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    boolean sendMultiMessage();

}
