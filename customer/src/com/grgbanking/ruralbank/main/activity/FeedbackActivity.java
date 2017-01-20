package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.contact.activity.UserProfileSettingActivity;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class FeedbackActivity extends UI implements View.OnClickListener {

    protected ImageView iv_message, iv_workorder, iv_me;
    private EditText et_feedback;
    private ImageView iv_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.feedback;
        setToolBar(R.id.toolbar, options);
        initId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onInit();
    }
    private void save(String advice){
        ServerApi.feedback(DemoCache.getUserid(),advice,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        Intent i = new Intent(FeedbackActivity.this, first_workorder_activity.class);
                        startActivity(i);
                        finish();
                    } else {
                        String ret_msg = response.getString("ret_msg");
                        Toast.makeText(FeedbackActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")){
                            Intent intent=new Intent(FeedbackActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(FeedbackActivity.this, "上传数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(FeedbackActivity.this, "上传数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onInit() {
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_workorder.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_category_focus));
        iv_me.setOnClickListener(this);
        iv_workorder.setOnClickListener(this);
        iv_message.setOnClickListener(this);
    }
    private void initId(){
        et_feedback=(EditText)findViewById(R.id.et_feedback);
        iv_confirm=(ImageView)findViewById(R.id.iv_confirm);
        iv_confirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_workorder:
//                Intent i = new Intent(this, ContactListActivity.class);
//                startActivity(i);
                break;
            case R.id.iv_me:
                Intent i2 = new Intent(this, UserProfileSettingActivity.class);
                startActivity(i2);
                finish();
                break;
            case R.id.iv_message:
                Intent i3 = new Intent(this, MainActivity.class);
                startActivity(i3);
                finish();
                break;
        }
        if (v==iv_confirm){
            save(et_feedback.getText().toString());
        }
    }
}
