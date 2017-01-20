package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.session.SessionHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liufei on 2016/8/3.
 */
public class input_evaluate_activity extends UI implements View.OnClickListener {
    private static final String TAG = input_evaluate_activity.class.getSimpleName();
    private ImageView star1, star2, star3, star4, star5, iv_forward;
    private EditText et_confirm_complete;
    private String star;
    private int number;
    private String jobOrderId;
    private ImageView talkIv,phoneIv,complaintIv;
    private TextView evaluate1Tv,evaluate2Tv,evaluate3Tv,evaluate4Tv,evaluate5Tv,evaluate6Tv;
    private boolean tag1,tag2,tag3,tag4,tag5,tag6;
    private Context mContext;
    private String complaintCall = null;
    private String supUserPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_evaluate);
        mContext = this;
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.evaluate;
        setToolBar(R.id.toolbar, options);
        getParams();
        number = 1;
        initID();
    }

    private void getParams() {
        jobOrderId = this.getIntent().getStringExtra("jobOrderId");
        ServerApi.getEvaluate(jobOrderId,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                String ret_msg = null;
                LogUtil.e("input_evaluate_activity",response.toString());
                try {
                    ret_code = response.getString("ret_code");
                    ret_msg = response.getString("ret_msg");
                    if (ret_code.equals("0")) {
                        JSONObject data = response.getJSONObject("lists");
                        complaintCall = data.getString("complaintCall");
                        supUserPhone = data.getString("supUserPhone");
                    } else {
                        Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                LogUtil.e(TAG, "login fail:" + throwable.getMessage());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                LogUtil.e(TAG, "login fail:" + throwable.getMessage());
            }
        });
    }


    private void initID() {
        star1 = (ImageView) findViewById(R.id.star1);
        star1.setOnClickListener(this);
        star2 = (ImageView) findViewById(R.id.star2);
        star2.setOnClickListener(this);
        star3 = (ImageView) findViewById(R.id.star3);
        star3.setOnClickListener(this);
        star4 = (ImageView) findViewById(R.id.star4);
        star4.setOnClickListener(this);
        star5 = (ImageView) findViewById(R.id.star5);
        star5.setOnClickListener(this);

        et_confirm_complete = (EditText) findViewById(R.id.et_confirm_complete);

        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_forward.setOnClickListener(this);
        talkIv = (ImageView) findViewById(R.id.iv_talk);
        phoneIv = (ImageView) findViewById(R.id.iv_phone);
        complaintIv = (ImageView) findViewById(R.id.iv_complaint);
        talkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supUserPhone!=null){
                    SessionHelper.startP2PSession(mContext, supUserPhone);
                }else{
                    Toast.makeText(mContext, "获取工程师信息失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        phoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supUserPhone!=null){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+supUserPhone));
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, "获取工程师信息失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
        complaintIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(complaintCall!=null){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+complaintCall));
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, "获取客服信息失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
        evaluate1Tv = (TextView) findViewById(R.id.evaluate1);
        evaluate2Tv = (TextView) findViewById(R.id.evaluate2);
        evaluate3Tv = (TextView) findViewById(R.id.evaluate3);
        evaluate4Tv = (TextView) findViewById(R.id.evaluate4);
        evaluate5Tv = (TextView) findViewById(R.id.evaluate5);
        evaluate6Tv = (TextView) findViewById(R.id.evaluate6);
        evaluate1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag1,evaluate1Tv);
                tag1 = !tag1;

            }
        });
        evaluate2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag2,evaluate2Tv);
                tag2 = !tag2;
            }
        });
        evaluate3Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag3,evaluate3Tv);
                tag3 = !tag3;
            }
        });
        evaluate4Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag4,evaluate4Tv);
                tag4 = !tag4;
            }
        });
        evaluate5Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag5,evaluate5Tv);
                tag5 = !tag5;
            }
        });
        evaluate6Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(tag6,evaluate6Tv);
                tag6 = !tag6;
            }
        });
    }

    private void changeBg(boolean flag,TextView textView){
        Drawable img_on;
        Resources res = getResources();
        if(flag){
            img_on = res.getDrawable(R.drawable.evaluate_like);
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            textView.setCompoundDrawables(null, null, img_on, null);
            textView.setBackgroundResource(R.drawable.evaluate_line2);
        }else{
            img_on = res.getDrawable(R.drawable.evaluate_unlike);
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            textView.setCompoundDrawables(null, null, img_on, null);
            textView.setBackgroundResource(R.drawable.evaluate_line);
        }
    }

    private void setEvaluate(String id, String userId, String userName , String content, String star) {
        ServerApi.setEvaluate(id, userId, userName, content, star, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        Toast.makeText(input_evaluate_activity.this, "评价完成", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(input_evaluate_activity.this, first_workorder_activity.class);
                        i.putExtra("state","005");
                        startActivity(i);
                        finish();
                    } else {
                        String ret_msg = response.getString("ret_msg");
                        Toast.makeText(input_evaluate_activity.this, ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")){
                            Intent intent=new Intent(input_evaluate_activity.this, LoginActivity.class);
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
                Toast.makeText(input_evaluate_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(input_evaluate_activity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == star1) {
            number++;
            if (number % 2 == 0) {
                star1.setImageResource(R.drawable.star2);
                star2.setImageResource(R.drawable.star1);
                star3.setImageResource(R.drawable.star1);
                star4.setImageResource(R.drawable.star1);
                star5.setImageResource(R.drawable.star1);

            } else {
                star1.setImageResource(R.drawable.star1);
                star2.setImageResource(R.drawable.star1);
                star3.setImageResource(R.drawable.star1);
                star4.setImageResource(R.drawable.star1);
                star5.setImageResource(R.drawable.star1);
                star = String.valueOf(1);
            }
        } else if (v == star2) {
            star1.setImageResource(R.drawable.star2);
            star2.setImageResource(R.drawable.star2);
            star3.setImageResource(R.drawable.star1);
            star4.setImageResource(R.drawable.star1);
            star5.setImageResource(R.drawable.star1);
            star = String.valueOf(2);
        } else if (v == star3) {
            star1.setImageResource(R.drawable.star2);
            star2.setImageResource(R.drawable.star2);
            star3.setImageResource(R.drawable.star2);
            star4.setImageResource(R.drawable.star1);
            star5.setImageResource(R.drawable.star1);
            star = String.valueOf(3);
        } else if (v == star4) {
            star1.setImageResource(R.drawable.star2);
            star2.setImageResource(R.drawable.star2);
            star3.setImageResource(R.drawable.star2);
            star4.setImageResource(R.drawable.star2);
            star5.setImageResource(R.drawable.star1);
            star = String.valueOf(4);
        } else if (v == star5) {
            star1.setImageResource(R.drawable.star2);
            star2.setImageResource(R.drawable.star2);
            star3.setImageResource(R.drawable.star2);
            star4.setImageResource(R.drawable.star2);
            star5.setImageResource(R.drawable.star2);
            star = String.valueOf(5);
        } else if (v == iv_forward) {
            if (star==null){
                Toast.makeText(this,"评分不能为空",Toast.LENGTH_SHORT).show();
            }else{
                String str = null;
                if(tag1){
                    str = evaluate1Tv.getText().toString() + ",";
                }
                if(tag2){
                    str = str + evaluate2Tv.getText().toString() + ",";
                }
                if(tag3){
                    str = str + evaluate3Tv.getText().toString() + ",";
                }
                if(tag4){
                    str = str + evaluate4Tv.getText().toString() + ",";
                }
                if(tag5){
                    str = str + evaluate5Tv.getText().toString() + ",";
                }
                if(tag6){
                    str = str + evaluate6Tv.getText().toString() + ",";
                }
                setEvaluate(jobOrderId, Preferences.getUsername(), Preferences.getUserid(), str + et_confirm_complete.getText().toString(), star);
            }
        }
    }
}
