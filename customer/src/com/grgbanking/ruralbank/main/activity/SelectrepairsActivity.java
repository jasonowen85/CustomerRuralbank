package com.grgbanking.ruralbank.main.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.grgbanking.ruralbank.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

public class SelectrepairsActivity extends UI implements View.OnClickListener{
    private Spinner spinner;
    private String mSupplierid;
    private String mSuppliername;
    private String mUserid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrepairs);
        init();
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.returns;
        setToolBar(R.id.toolbar, options);
        getParams();
    }

    private void getParams(){
        mSupplierid = this.getIntent().getStringExtra("supplierid");
        mSuppliername = this.getIntent().getStringExtra("suppliername");
        mUserid = this.getIntent().getStringExtra("userid");
    }

    private void init() {
        spinner=(Spinner)findViewById(R.id.spinner_select);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 2:
                            Intent intent=new Intent();
                            intent.setClass(SelectrepairsActivity.this,Addwork_order_Activity.class);
                            if(mUserid!=null) {
                                intent.putExtra("userid", mUserid);
                                intent.putExtra("suppliername", mSuppliername);
                                intent.putExtra("supplierid", mSupplierid);
                            }
                            startActivities(new Intent[]{intent});
                            finish();
                            break;
                        case 1:
                            Intent it=new Intent();
                            it.setClass(SelectrepairsActivity.this,Visit_worderActivity.class);
                            if(mUserid!=null) {
                                it.putExtra("userid", mUserid);
                                it.putExtra("suppliername", mSuppliername);
                                it.putExtra("supplierid", mSupplierid);
                            }
                            startActivities(new Intent[]{it});
                            finish();
                            break;
                    }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}

