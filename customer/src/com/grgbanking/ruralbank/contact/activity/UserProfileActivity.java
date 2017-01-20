package com.grgbanking.ruralbank.contact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.userInfo;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.contact.helper.UserUpdateHelper;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.model.Extras;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileActivity extends UI {
    private final String TAG = UserProfileActivity.class.getSimpleName();

    // constant
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;

    private String account;

    // view
    private HeadImageView userHead;
    private RelativeLayout nickLayout;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout workLayout;
    private RelativeLayout companyLayout;
    private RelativeLayout deptLayout;
    private RelativeLayout jobLayout;


    private TextView nickText;
    private TextView phoneText;
    private TextView emailText;
    private TextView workText;
    private TextView companyText;
    private TextView deptText;
    private TextView jobText;


    AbortableFuture<String> uploadAvatarFuture;
    private NimUserInfo ninInfo;
    private com.grgbanking.ruralbank.common.bean.userInfo userInfo;

    public static void start(Context context, String account) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.user_information;
        setToolBar(R.id.toolbar, options);

        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        if (account == null || account.equals("")) {
            account = DemoCache.getAccount();
        }
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void findViews() {
        userHead = findView(R.id.user_head);
        nickLayout = findView(R.id.nick_layout);
        phoneLayout = findView(R.id.phone_layout);
        emailLayout = findView(R.id.email_layout);
        workLayout = findView(R.id.work_layout);
        companyLayout = findView(R.id.company_layout);
        deptLayout = findView(R.id.dept_layout);
        jobLayout = findView(R.id.job_layout);

        ((TextView) nickLayout.findViewById(R.id.attribute)).setText(R.string.nickname);
        ((TextView) phoneLayout.findViewById(R.id.attribute)).setText(R.string.phone);
        ((TextView) emailLayout.findViewById(R.id.attribute)).setText(R.string.email);
        ((TextView) workLayout.findViewById(R.id.attribute)).setText("工 号");
        ((TextView) companyLayout.findViewById(R.id.attribute)).setText("支 行");
        ((TextView) deptLayout.findViewById(R.id.attribute)).setText("部 门");
        ((TextView) jobLayout.findViewById(R.id.attribute)).setText("职 位");

        nickText = (TextView) nickLayout.findViewById(R.id.value);
        phoneText = (TextView) phoneLayout.findViewById(R.id.value);
        emailText = (TextView) emailLayout.findViewById(R.id.value);
        companyText = (TextView) companyLayout.findViewById(R.id.value);
        workText = (TextView) workLayout.findViewById(R.id.value);
        deptText = (TextView) deptLayout.findViewById(R.id.value);
        jobText = (TextView) jobLayout.findViewById(R.id.value);

        ImageView phoneImg = (ImageView) phoneLayout.findViewById(R.id.iv_right);
        phoneImg.setVisibility(View.INVISIBLE);
        ImageView emailImg = (ImageView) emailLayout.findViewById(R.id.iv_right);
        emailImg.setVisibility(View.INVISIBLE);

        ImageView nicgkImg = (ImageView) nickLayout.findViewById(R.id.iv_right);
        nicgkImg.setVisibility(View.INVISIBLE);
        ImageView companyImg = (ImageView) companyLayout.findViewById(R.id.iv_right);
        companyImg.setVisibility(View.INVISIBLE);
        ImageView workImg = (ImageView) workLayout.findViewById(R.id.iv_right);
        workImg.setVisibility(View.INVISIBLE);
        ImageView deptImg = (ImageView) deptLayout.findViewById(R.id.iv_right);
        deptImg.setVisibility(View.INVISIBLE);
        ImageView jobImg = (ImageView) jobLayout.findViewById(R.id.iv_right);
        jobImg.setVisibility(View.INVISIBLE);

    }

    private void getUserInfo() {
        userHead.loadBuddyAvatar(account);
        if (Preferences.getUserid() == null) {
            return;
        }
        ServerApi.getUserInfo(account, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                try {
                    ret_code = response.getString("ret_code");

                    if (ret_code.equals("0")) {
                        JSONObject data = response.getJSONObject("lists");
                        userInfo = new userInfo();
                        userInfo.setName(data.getString("name"));
                        userInfo.setPhone(data.getString("phone"));
                        userInfo.setEmail(data.getString("email"));
                        userInfo.setWork(data.getString("jobnum"));
                        if (data.has("supplierName")) {
                            userInfo.setCompany(data.getString("supplierName"));
                        }
                        if (data.has("branch")) {
                            userInfo.setCompany(data.getString("branch"));
                        }
                        userInfo.setDept(data.getString("depart"));
                        userInfo.setJob(data.getString("position"));
                        updateUI();
                    } else {
                        ret_msg = response.getString("ret_msg");
                        Toast.makeText(UserProfileActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")){
                            Intent intent=new Intent(UserProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                LogUtil.e(TAG, "getUserInfo fail:" + throwable.getMessage());
            }
        });
    }

    private void updateUI() {
        nickText.setText(userInfo.getName());
        if (userInfo.getPhone() != null) {
            phoneText.setText(userInfo.getPhone());
        }
        if (userInfo.getEmail() != null) {
            emailText.setText(userInfo.getEmail());
        }
        if (userInfo.getWork() != null) {
            workText.setText(userInfo.getWork());
        }
        if (userInfo.getCompany() != null) {
            companyText.setText(userInfo.getCompany());
        }
        if (userInfo.getDept() != null) {
            deptText.setText(userInfo.getDept());
        }
        if (userInfo.getJob() != null) {
            jobText.setText(userInfo.getJob());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            updateAvatar(path);
        }
    }

    /**
     * 更新头像
     */
    private void updateAvatar(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File file = new File(path);
        if (file == null) {
            return;
        }

        DialogMaker.showProgressDialog(this, null, null, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);

        LogUtil.i(TAG, "start upload avatar, local file path=" + file.getAbsolutePath());
        new Handler().postDelayed(outimeTask, AVATAR_TIME_OUT);
        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload avatar success, url =" + url);

                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(UserProfileActivity.this, R.string.head_update_success, Toast.LENGTH_SHORT).show();
                                onUpdateDone();
                            } else {
                                Toast.makeText(UserProfileActivity.this, R.string.head_update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(UserProfileActivity.this, R.string.user_info_update_failed, Toast
                            .LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(UserProfileActivity.this, resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(R.string.user_info_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
        getUserInfo();
    }
}
