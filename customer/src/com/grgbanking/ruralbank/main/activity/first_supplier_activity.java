package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.common.util.widget.IconCenterEditText;
import com.grgbanking.ruralbank.contact.activity.UserProfileSettingActivity;
import com.netease.nim.uikit.common.activity.UI;

/**
 * Created by liufei on 2016/8/4.
 */
public class first_supplier_activity extends UI implements View.OnClickListener{
    private IconCenterEditText icet_search;
    protected ImageView iv_message, iv_workorder, iv_me,iv_supplier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_supplier);
        setToolBar(R.id.toolbar, R.string.app_name, R.drawable.actionbar_dark_logo);
        setTitle(R.string.supplier);
        initID();
    }
    private void initID(){
        icet_search=(IconCenterEditText)findViewById(R.id.icet_search);
        icet_search.setOnSearchClickListener(new IconCenterEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {

                Toast.makeText(first_supplier_activity.this, "i'm going to seach", Toast.LENGTH_SHORT).show();
            }
        });
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_supplier = (ImageView) findViewById(R.id.iv_supplier);
        iv_me.setOnClickListener(this);
        iv_workorder.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        iv_supplier.setOnClickListener(this);
        iv_supplier.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_supplier_focus));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_workorder:
                Intent i = new Intent(this, first_workorder_activity.class);
                i.putExtra("state","001");
                startActivity(i);
                break;
            case R.id.iv_me:
                Intent i2 = new Intent(this, UserProfileSettingActivity.class);
                startActivity(i2);
                break;
            case R.id.iv_message:
                Intent i3=new Intent(this,MainActivity.class);
                startActivity(i3);
                break;
        }
    }
}
