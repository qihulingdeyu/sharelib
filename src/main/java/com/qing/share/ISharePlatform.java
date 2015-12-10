package com.qing.share;

import android.content.Intent;

import com.qing.share.content.ImageObject;
import com.qing.share.content.MediaObject;
import com.qing.share.content.TextObject;

/**
 * Created by zwq on 2015/10/30 11:31.<br/><br/>
 * 所有分享平台都有实现此接口
 */
public interface ISharePlatform {

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
     */
    void authorize(int type);

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
     * @param textObject
     */
    void shareText(TextObject textObject);

    /**
     * 图片消息对象。
     * 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
     *
     * @param imageObject
     */
    void shareImage(ImageObject imageObject);

    /**
     * 多媒体（网页）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    void shareWebPage(MediaObject mediaObject);

    /**
     * 多媒体（音乐）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    void shareMusic(MediaObject mediaObject);

    /**
     * 多媒体（视频）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    void shareVideo(MediaObject mediaObject);

    /**
     * 多媒体（音频）消息对象。
     *
     * @param mediaObject (新浪微博设置缩略图时注意：最终压缩过的缩略图大小不得超过 32kb。)
     */
    void shareVoice(MediaObject mediaObject);

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 只支持分享单条消息，即文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    void sendSingleMessage();

    /**
     * 第三方应用发送请求消息，唤起分享界面。
     * 支持同时分享多条消息，同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    void sendMultiMessage();

}
