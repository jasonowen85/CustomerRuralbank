package com.grgbanking.ruralbank.config.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.grgbanking.ruralbank.DemoCache;

/**
 * Created by hzxuwen on 2015/4/13.
 */
public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_BRANCH = "branch";
    private static final String KEY_BRANCHID = "branchid";
    private static final String KEY_USER_NAME = "username";
    public static void clear() {
        removeString(KEY_USER_ACCOUNT);
        removeString(KEY_USER_ID);
        removeString(KEY_USER_NAME);
        removeString(KEY_BRANCH);
        removeString(KEY_BRANCHID);
    }

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    public static void saveBranchid(String branchid) {
        saveString(KEY_BRANCHID, branchid);
    }

    public static String getBranchid() {
        return getString(KEY_BRANCHID);
    }


    public static void saveBranch(String branch) {
        saveString(KEY_BRANCH, branch);
    }

    public static String getBranch() {
        return getString(KEY_BRANCH);
    }

    public static void saveUserid(String userid) {
        saveString(KEY_USER_ID, userid);
    }

    public static String getUserid() {
        return getString(KEY_USER_ID);
    }

    public static void saveUsername(String username) {
        saveString(KEY_USER_NAME, username);
    }

    public static String getUsername() {
        return getString(KEY_USER_NAME);
    }

    private static void removeString(String key) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("Demo", Context.MODE_PRIVATE);
    }
}
