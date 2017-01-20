package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dell on 2016/8/25.
 */
public class input_courier_number_activity extends UI {
    private EditText et_express,et_courier_number;
    private ImageView iv_forward;
    private String jobOrderId,express,courier_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_courier_number);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.express_courier_number;
        setToolBar(R.id.toolbar, options);

        Intent intent=getIntent();
        jobOrderId=intent.getStringExtra("jobOrderId");

        initID();
    }
    private void initID() {
        et_express = (EditText) findViewById(R.id.et_express);
        et_express.setText(express);
        et_courier_number = (EditText) findViewById(R.id.et_courier_number);
        et_courier_number.setText(courier_number);
        iv_forward=(ImageView) findViewById(R.id.iv_forward);
        iv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCourierNum(jobOrderId,et_express.getText().toString(),et_courier_number.getText().toString());
            }
        });
    }
    private void inputCourierNum(String jobOrderId,String express,String courierNum){
        ServerApi.inputCourierNum(jobOrderId,express,courierNum,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        Toast.makeText(input_courier_number_activity.this, "录入完成", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(input_courier_number_activity.this, first_workorder_activity.class);
                        i.putExtra("state","001");
                        startActivity(i);
                        finish();
                    } else {
                        String ret_msg = response.getString("ret_msg");
                        Toast.makeText(input_courier_number_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")){
                            Intent intent=new Intent(input_courier_number_activity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(input_courier_number_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(input_courier_number_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
