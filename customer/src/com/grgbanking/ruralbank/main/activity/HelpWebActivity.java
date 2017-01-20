package com.grgbanking.ruralbank.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.common.AppConfig;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.lang.reflect.Field;

/**
 * Created by LiuPeng on 2016/9/22.
 */
public class HelpWebActivity extends UI {
    private WebView webView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.help;
        setToolBar(R.id.toolbar, options);
        setConfigCallback((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        setProgressBarVisibility(true);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://" + AppConfig.Server_IP + ":" + AppConfig.Server_Port + "/equipwarranty/api/help/bankUser");
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(progress);
            }
        });

        WebSettings websettings = webView.getSettings();
        websettings.setBuiltInZoomControls(false);

    }

    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);
            if (null == configCallback) {
                return;
            }
            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.removeAllViews();
        webView.destroy();
        webView.setVisibility(View.GONE);
        setConfigCallback(null);
        super.onDestroy();
    }

}
