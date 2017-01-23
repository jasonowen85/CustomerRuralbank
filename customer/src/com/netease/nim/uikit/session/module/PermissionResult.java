package com.netease.nim.uikit.session.module;

/**
 * Created by hasee on 2017/1/18.
 */
public interface PermissionResult {
    // 发送权限的请求
    void requestPermissionAudio();// 录音的的请求
    void requestPermissionCarame(boolean isAllow);
    void requestPermissionSDcard(boolean isAllow);
//    void requestPermissionLocation(String[] permession);
//    void requestPermissionCallPhone(String[] permession);
}
