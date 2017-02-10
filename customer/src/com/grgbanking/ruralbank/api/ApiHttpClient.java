package com.grgbanking.ruralbank.api;

import android.content.Context;
import android.util.Log;

import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.common.AppConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.client.params.ClientPNames;

import java.util.Locale;

public class ApiHttpClient {

    public static String HOST = AppConfig.Server_IP;
    public static String API_URL = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api" + "%s";
    public static String API_URL_IMG = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api/image/get?dir=" + "%s";
    public static String API_URL_VOICE = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api/voice/download?dir=" + "%s";
    public static String API_URL_DOWNLOAD = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api/file/download" + "%s";
    public static String API_URL_ORDER = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api/jobOrder/detail?id=" + "%s";
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static AsyncHttpClient client;

    public ApiHttpClient() {
    }

    public static void resetHost() {
        HOST = AppConfig.Server_IP;
        API_URL = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/jeesite/a" + "%s";
        API_URL_IMG = "http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/Yct-web-file" + "%s";
    }

    public static void setTimeOut(int timeout) {
        client.setTimeout(timeout);
    }

    public static AsyncHttpClient getHttpClient() {
        return client;
    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static void clearUserCookies(Context context) {
        // (new HttpClientCookieStore(context)).a();
    }

    public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
        client.delete(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("DELETE ").append(partUrl).toString());
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("GET ").append(partUrl).toString());
    }

    public static void get(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), params, handler);
//		String test = getAbsoluteApiUrl(partUrl);
    }

    public static void download(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(String.format(API_URL_DOWNLOAD, partUrl), params, handler);
//		String test = getAbsoluteApiUrl(partUrl);
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        String url = String.format(API_URL, partUrl);
        Log.d("BASE_CLIENT", "request:" + url);
        return url;
    }

    public static String getAbsoluteApiUrl2(String partUrl) {
        String url = String.format(API_URL_IMG, partUrl);
        Log.d("BASE_CLIENT", "request:" + url);
        return url;
    }

    public static String getApiUrl() {
        return API_URL;
    }

    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
        log(new StringBuilder("GET ").append(url).toString());
    }

    public static void get2(String partUrl, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), handler);
        //log(new StringBuilder("GET ").append(partUrl).append("&").append(entity).toString());
    }

    public static void log(String log) {
        Log.d("BaseApi", log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("POST ").append(getAbsoluteApiUrl(partUrl)).toString());
    }

    public static void post(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("POST ").append(getAbsoluteApiUrl(partUrl)).append("?").append(params).toString());
    }

    public static void post2(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl2(partUrl), params, handler);
        log(new StringBuilder("POST ").append(getAbsoluteApiUrl(partUrl)).append("?").append(params).toString());
    }

    public static void post(String partUrl, HttpEntity entity, AsyncHttpResponseHandler handler) {
        client.post(null, getAbsoluteApiUrl(partUrl), entity, null, handler);
        log(new StringBuilder("POST ").append(getAbsoluteApiUrl(partUrl)).append("?").append(entity).toString());
    }

    public static void postDirect(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
        log(new StringBuilder("POST ").append(url).append("&").append(params).toString());
    }

    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("PUT ").append(partUrl).toString());
    }

    public static void put(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("PUT ").append(partUrl).append("&").append(params).toString());
    }

    public static void setApiUrl(String apiUrl) {
        API_URL = apiUrl;
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", HOST);
        client.addHeader("Connection", "Keep-Alive");
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

    }

    public static void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }

    public static void setCookie(String cookie) {
        client.addHeader("Cookie", cookie);
    }

    private static String appCookie;

    public static void cleanCookie() {
        appCookie = "";
    }

    public static String getCookie(NimApplication appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }


}