package com.grgbanking.ruralbank.contact.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.contact.constant.UserConstant;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileEditItemActivity extends UI {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;

    // data
    private int key;
    private String data;
    private String _phone;
    private String _email;
    private Map<Integer, UserInfoFieldEnum> fieldMap;

    // VIEW
    private ClearableEditTextWithIcon editText;


    public static final void startActivity(Context context, int key, String data, String phone, String email) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileEditItemActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        if (key == UserConstant.KEY_PHONE || key == UserConstant.KEY_EMAIL) {
            setContentView(R.layout.user_profile_edittext_layout);
            findEditText();
        }
        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options);
        initActionbar();
        setTitles();
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }

    private void parseIntent() {
        key = getIntent().getIntExtra(EXTRA_KEY, -1);
        data = getIntent().getStringExtra(EXTRA_DATA);
        _phone = getIntent().getStringExtra("phone");
        _email = getIntent().getStringExtra("email");
    }

    private void setTitles() {
        switch (key) {
            case UserConstant.KEY_PHONE:
                setTitle(R.string.phone_number);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UserConstant.KEY_EMAIL:
                setTitle(R.string.email);
                break;

        }
    }

    private void findEditText() {
        editText = findView(R.id.edittext);
        if (key == UserConstant.KEY_PHONE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        } else if (key == UserConstant.KEY_EMAIL) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        }
        editText.setText(data);

        editText.setDeleteImage(R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.save);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetAvailable(UserProfileEditItemActivity.this)) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    update(editText.getText().toString().trim());
                }
            }
        });
    }


    private void update(Serializable content) {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
            fieldMap.put(UserConstant.KEY_PHONE, UserInfoFieldEnum.MOBILE);
            fieldMap.put(UserConstant.KEY_EMAIL, UserInfoFieldEnum.EMAIL);
        }
        DialogMaker.showProgressDialog(this, null, true);
        if (DemoCache.getUserid() == null) {
            return;
        }
        String email = _email;
        String phone = _phone;
        if (UserConstant.KEY_PHONE == key) {
            phone = editText.getText().toString();
        } else {
            email = editText.getText().toString();
        }
        ServerApi.updateUserInfo(DemoCache.getUserid(), phone, email, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = null;
                try {
                    ret_code = response.getString("ret_code");
                    if (ret_code.equals("0")) {
                        DialogMaker.dismissProgressDialog();
                        onUpdateCompleted();
                    } else {
                        Toast.makeText(UserProfileEditItemActivity.this, ret_code, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")){
                            Intent intent=new Intent(UserProfileEditItemActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        Toast.makeText(UserProfileEditItemActivity.this, R.string.user_info_update_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class MyDatePickerDialog extends DatePickerDialog {
        private int maxYear = 2015;
        private int minYear = 1900;
        private int currYear;
        private int currMonthOfYear;
        private int currDayOfMonth;

        public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            currYear = year;
            currMonthOfYear = monthOfYear;
            currDayOfMonth = dayOfMonth;
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            if (year >= minYear && year <= maxYear) {
                currYear = year;
                currMonthOfYear = month;
                currDayOfMonth = day;
            } else {
                if (currYear > maxYear) {
                    currYear = maxYear;
                } else if (currYear < minYear) {
                    currYear = minYear;
                }
                updateDate(currYear, currMonthOfYear, currDayOfMonth);
            }
        }

        public void setMaxYear(int year) {
            maxYear = year;
        }

        public void setMinYear(int year) {
            minYear = year;
        }

        public void setTitle(CharSequence title) {
            super.setTitle("生 日");
        }
    }
}
