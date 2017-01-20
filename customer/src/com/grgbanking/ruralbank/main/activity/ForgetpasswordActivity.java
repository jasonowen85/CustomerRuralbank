package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class ForgetpasswordActivity extends UI implements View.OnClickListener {
    private static final String TAG = ForgetpasswordActivity.class.getSimpleName();

    private Button btn_confirm;
    private EditText edit_account, edit_email, edit_phone, edit_newpassword, edit_confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.forgetpassword;
        setToolBar(R.id.toolbar, options);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onInit();
    }

    protected void onInit() {
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_newpassword = (EditText) findViewById(R.id.edit_newpassword);
        edit_confirmpassword = (EditText) findViewById(R.id.edit_confirmpassword);
    }

    private boolean validate() {
        if (edit_account.getText().toString().trim().equals("") ||
                edit_email.getText().toString().trim().equals("") ||
                edit_phone.getText().toString().trim().equals("") ||
                edit_newpassword.getText().toString().trim().equals("") ||
                edit_confirmpassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请输入完整数据", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!edit_newpassword.getText().toString().trim().equals(edit_confirmpassword.getText().toString().trim())) {
            Toast.makeText(this, "两次输入的密码不匹配", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (validate()) {

                    ServerApi.resetPassword( edit_account.getText().toString().trim(), edit_phone.getText().toString().trim(), edit_email.getText().toString().trim(),
                            edit_confirmpassword.getText().toString().trim(), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    String ret_code = null;
                                    String ret_msg=null;
                                    try {
                                        ret_code = response.getString("ret_code");
                                        ret_msg = response.getString("ret_msg");
                                        if (ret_code.equals("0")) {
                                            Toast.makeText(ForgetpasswordActivity.this, "重置密码成功", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(ForgetpasswordActivity.this, LoginActivity.class);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(ForgetpasswordActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                                            if (ret_code.equals("0011")){
                                                Intent intent=new Intent(ForgetpasswordActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                                    LogUtil.e(TAG, "reset password fail:" + throwable.getMessage());
                                    Toast.makeText(ForgetpasswordActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                                    LogUtil.e(TAG, "reset password:" + throwable.getMessage());
                                    Toast.makeText(ForgetpasswordActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    break;
                }
        }
    }
}
