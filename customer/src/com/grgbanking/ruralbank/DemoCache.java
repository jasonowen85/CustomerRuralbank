package com.grgbanking.ruralbank;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by jezhee on 2/20/15.
 */
public class DemoCache {

    private static Context context;

    private static String account;
    private static String userid;
    private static String branch;
    private static String branchid;
    private static String username;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
        userid = null;
        branch=null;
        username=null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
    }


    public static String getBranch() {
        return branch;
    }

    public static void setBranch(String branch) {
        DemoCache.branch = branch;
    }

    public static String getBranchid() {
        return branchid;
    }

    public static void setBranchid(String branchid) {
        DemoCache.branchid = branchid;
    }


    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        DemoCache.userid = userid;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DemoCache.username = username;
    }


    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();
    }
}
