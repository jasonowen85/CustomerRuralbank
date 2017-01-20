package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.util.update.UpdateChecker;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.contact.activity.UserProfileSettingActivity;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.login.LogoutHelper;
import com.grgbanking.ruralbank.main.model.Extras;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户资料页面
 * Created by huangjun on 2015/8/11.
 */
public class MeProfileActivity extends UI {

    private static final String TAG = MeProfileActivity.class.getSimpleName();


    private String account;

    // 基本信息
    private HeadImageView headImageView;
    private TextView nameText;
    private TextView accountText;
    private RelativeLayout editinfoLayout;
    private RelativeLayout modifypasswordLayout;
    private RelativeLayout updateversionLayout;
    private RelativeLayout aboutLayout;
    private RelativeLayout feedbackLayout;
    private RelativeLayout helpLayout;
    protected ImageView iv_message, iv_workorder, iv_me, iv_supplier;
    private Button exitBtn;

    public static void start(Context context, String account) {
        Intent intent = new Intent();
        intent.setClass(context, MeProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_profile_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.user_me;
        setToolBarCenter(R.id.toolbar, options);
        toolbar.setNavigationIcon(null);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        if (account == null || account.equals("")) {
            account = DemoCache.getAccount();
        }

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        headImageView = findView(R.id.user_head_image);
        nameText = findView(R.id.user_name);
        accountText = findView(R.id.user_account);
        exitBtn = findView(R.id.btn_exit);
        editinfoLayout = findView(R.id.editinfo);
        editinfoLayout.setOnClickListener(onClickListener);
        modifypasswordLayout = findView(R.id.modifypassword);
        modifypasswordLayout.setOnClickListener(onClickListener);
        updateversionLayout = findView(R.id.updateversion);
        updateversionLayout.setOnClickListener(onClickListener);
        aboutLayout = findView(R.id.about);
        aboutLayout.setOnClickListener(onClickListener);
        feedbackLayout = findView(R.id.feedback);
        feedbackLayout.setOnClickListener(onClickListener);
        feedbackLayout.findViewById(R.id.line).setVisibility(View.INVISIBLE);
        helpLayout = (RelativeLayout) findViewById(R.id.help);
        helpLayout.setOnClickListener(onClickListener);
        ((TextView) modifypasswordLayout.findViewById(R.id.attribute)).setText("修改登录密码");
        ((TextView) updateversionLayout.findViewById(R.id.attribute)).setText("软件版本更新");
        ((TextView) aboutLayout.findViewById(R.id.attribute)).setText("关于我们");
        ((TextView) feedbackLayout.findViewById(R.id.attribute)).setText("意见反馈");
        ((TextView) helpLayout.findViewById(R.id.attribute)).setText("常见问题");
        exitBtn.setOnClickListener(onClickListener);
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_me.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_user_focus));
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_workorder.setOnClickListener(onClickListener);
        iv_supplier = (ImageView) findViewById(R.id.iv_supplier);
        iv_supplier.setOnClickListener(onClickListener);
        iv_message.setOnClickListener(onClickListener);
    }

    private void updateUserInfo() {
        if (NimUserInfoCache.getInstance().hasUser(account)) {
            updateUserInfoView();
            return;
        }

        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallbackWrapper<NimUserInfo>() {
            @Override
            public void onResult(int code, NimUserInfo result, Throwable exception) {
                updateUserInfoView();
            }
        });
    }

    private void updateUserInfoView() {
        if(account.contains(NimApplication.sendApkName)){
            accountText.setText(account.substring(NimApplication.sendApkName.length()));
        } else {
            accountText.setText(account);
        }

        headImageView.loadBuddyAvatar(account);

        if (DemoCache.getAccount().equals(account)) {
            nameText.setText(NimUserInfoCache.getInstance().getUserName(account));
        }

        final NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null) {
            LogUtil.e(TAG, "userInfo is null when updateUserInfoView");
            return;
        }

    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_exit:
                    if (DemoCache.getUserid() == null) {
                        DemoCache.clear();
                        Preferences.clear();
                        // 清理缓存&注销监听
                        LogoutHelper.logout();
                        NIMClient.getService(AuthService.class).logout();
                        // 启动登录
                        LoginActivity.start(MeProfileActivity.this);
                        finish();
                        return;
                    }
                    ServerApi.logout(DemoCache.getUserid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            String ret_code = null;
                            String ret_msg = null;
                            try {
                                ret_code = response.getString("ret_code");
                                ret_msg = response.getString("ret_msg");
                                if (ret_code.equals("0")) {
                                    DemoCache.clear();
                                    Preferences.clear();
                                    Intent i = new Intent(MeProfileActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    DemoCache.clear();
                                    Preferences.clear();
                                    // 清理缓存&注销监听
                                    LogoutHelper.logout();
                                    NIMClient.getService(AuthService.class).logout();
                                    // 启动登录
                                    LoginActivity.start(MeProfileActivity.this);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                            LogUtil.e(TAG, "exit fail:" + throwable.getMessage());
                            Toast.makeText(MeProfileActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                            LogUtil.e(TAG, "exit fail:" + throwable.getMessage());
                            Toast.makeText(MeProfileActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.updateversion:
                    UpdateChecker.checkForDialog(MeProfileActivity.this);
                    break;
                case R.id.modifypassword:
                    Intent i2 = new Intent(MeProfileActivity.this, ModifypasswordActivity.class);
                    startActivity(i2);

                    break;
                case R.id.about:
                    Intent i3 = new Intent(MeProfileActivity.this, AboutActivity.class);
                    startActivity(i3);

                    break;
                case R.id.help:{
                    Intent helpIntent = new Intent(MeProfileActivity.this, HelpWebActivity.class);
                    startActivity(helpIntent);
                    break;
                }
                case R.id.feedback:
                    Intent i6 = new Intent(MeProfileActivity.this, FeedbackActivity.class);
                    startActivity(i6);

                    break;
                case R.id.editinfo:
                    UserProfileSettingActivity.start(MeProfileActivity.this, account);

                    break;
                case R.id.iv_workorder:
                    Intent i4 = new Intent(MeProfileActivity.this, first_workorder_activity.class);
                    i4.putExtra("state","001");
                    startActivity(i4);
                    finish();
                    break;
                case R.id.iv_message:
                    Intent i5 = new Intent(MeProfileActivity.this, MainActivity.class);
                    startActivity(i5);
                    finish();
                    break;
                case R.id.iv_supplier:
//                Intent i4 = new Intent(this, first_supplier_activity.class);
//                startActivity(i4);
                    Intent intent = new Intent(MeProfileActivity.this, SupplierListActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
