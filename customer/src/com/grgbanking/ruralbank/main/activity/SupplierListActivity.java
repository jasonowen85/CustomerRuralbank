package com.grgbanking.ruralbank.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.adapter.MyExpandableListViewAdapter;
import com.grgbanking.ruralbank.main.adapter.SupplierListMoreAdapter;
import com.grgbanking.ruralbank.main.adapter.SupplierMainAdapter;
import com.grgbanking.ruralbank.main.model.SupplierBean;
import com.grgbanking.ruralbank.main.model.SupplierBean.AddressEntity.SupListEntity;
import com.grgbanking.ruralbank.session.SessionHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuPeng on 2016/8/4.
 * 供应商列表
 */
public class SupplierListActivity extends UI{
    private SupplierListMoreAdapter moreAdapter;
    private MyExpandableListViewAdapter myExpandableListViewAdapter;
    private ExpandableListView expandableListView;
    private ListView morelist;
    private List<SupplierBean.AddressEntity> mainList;
    private ListView mainlist;
    private SupplierMainAdapter mainAdapter;
    private Context mContext;
    protected ImageView iv_message, iv_workorder, iv_me,iv_supplier;
    private Button loadingBtn;
    private LinearLayout loadingLayout;
    private ProgressBar loading;
    private TextView moreTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier_list_activity);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.supplier;
        setToolBarCenter(R.id.toolbar, options);
        toolbar.setNavigationIcon(null);
        mContext = this;
        initView();
        initModle();// 添加数据
    }

    private void initView() {
        loadingBtn = (Button) findViewById(R.id.loading_btn);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loading = (ProgressBar) findViewById(R.id.loading);
        moreTv = (TextView) findViewById(R.id.more);
        loadingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initModle();
            }
        });
        mainlist = (ListView) findViewById(R.id.classify_mainlist);
        morelist = (ListView) findViewById(R.id.classify_morelist);
        expandableListView = (ExpandableListView) findViewById(R.id.expendlist);
        expandableListView.setGroupIndicator(null);
        iv_me = (ImageView) findViewById(R.id.iv_me);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_workorder = (ImageView) findViewById(R.id.iv_workorder);
        iv_supplier = (ImageView) findViewById(R.id.iv_supplier);
        iv_supplier.setImageDrawable(getResources().getDrawable(R.drawable.main_tab_item_supplier_focus));
        iv_workorder.setOnClickListener(onClickListener);
        iv_message.setOnClickListener(onClickListener);
        iv_supplier.setOnClickListener(onClickListener);
        iv_me.setOnClickListener(onClickListener);

        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                List<SupplierBean.AddressEntity.SupListEntity> lists = mainList.get(position).getArea();
                //initAdapter(lists);
                initAdapter1(lists);
                mainAdapter.setSelectItem(position);
                mainAdapter.notifyDataSetChanged();
            }
        });
        mainlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                moreAdapter.setSelectItem(position);
                moreAdapter.notifyDataSetChanged();
                SupListEntity entity = (SupListEntity)moreAdapter.getItem(position);
                Intent intent = new Intent(mContext,forward_activity.class);
                intent.putExtra("supplierId",entity.getId());
                intent.putExtra("supplierName",entity.getName());
                startActivity(intent);
            }
        });

        // 监听每个分组里子控件的点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(childPosition != 0){
                    LogUtil.e("SupplierListActivity","phone == "+myExpandableListViewAdapter.getChild(groupPosition,childPosition).toString());
                    SessionHelper.startP2PSession(mContext, myExpandableListViewAdapter.getChild(groupPosition,childPosition).toString());
                }
                return false;
            }
        });

        // 监听组点击
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @SuppressLint("NewApi")
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                if (myExpandableListViewAdapter.getChildrenCount(groupPosition) == 1)
                {
                    return true;
                }
                return false;
            }
        });
    }


    private void initAdapter(List<SupplierBean.AddressEntity.SupListEntity> lists) {
        moreAdapter = new SupplierListMoreAdapter(this, lists);
        morelist.setAdapter(moreAdapter);
        moreAdapter.notifyDataSetChanged();
    }

    private void initAdapter1(List<SupplierBean.AddressEntity.SupListEntity> lists) {
        myExpandableListViewAdapter = new MyExpandableListViewAdapter(this, lists);
        expandableListView.setAdapter(myExpandableListViewAdapter);
        myExpandableListViewAdapter.notifyDataSetChanged();
    }

    private void initModle() {
        mainList = new ArrayList<>();
        mainAdapter = new SupplierMainAdapter(this, mainList);
        mainAdapter.setSelectItem(0);
        mainlist.setAdapter(mainAdapter);
        loadingLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        moreTv.setVisibility(View.VISIBLE);
        loadingBtn.setVisibility(View.GONE);
        ServerApi.getdeviceTypeList(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getString("ret_code").equals("0")){
                        String json = response.toString();
                        LogUtil.e("SupplierListActivity", "ret_code==" + json);
                        //id":"5FC0F53FBCDC48658237D9E399A35FFC","userRoleId":"20001","phone":"12345678911","
                        // supplierName":"供应商1号","supplierId":"31052CA0FE98433FBB8423260A8E8980","
                        // depart":"","name":"谢小军","roleName":"客服","jobOrderNum":"0","userType":"002"},
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<SupplierBean>() {
                        }.getType();
                        SupplierBean b = gson.fromJson(json, type);
                        LogUtil.e("SupplierListActivity",(b==null)+"");
                        mainList.addAll(b.getAddress());
                        loadingLayout.setVisibility(View.GONE);
                        //initAdapter(mainList.get(0).getArea());
                        initAdapter1(mainList.get(0).getArea());
                        for (int i = 0; i < b.getAddress().size(); i++) {
                            LogUtil.e("SupplierListActivity","name=="+b.getAddress().get(i).getName());
                            for (int j = 0; j < b.getAddress().get(i).getArea().size(); j++) {
                                LogUtil.e("SupplierListActivity",
                                        ""+b.getAddress().get(i).getArea().get(j).getName()+
                                                "tell=="+b.getAddress().get(i).getArea().get(j).getTell() + "address=="
                                 + b.getAddress().get(i).getArea().get(j).getAddress());
                                for (int k = 0; k < b.getAddress().get(i).getArea().get(j).getCustomerList().size(); k++) {
                                    LogUtil.e("SupplierListActivity","name=="+b.getAddress().get(i).getArea().get(j).getCustomerList().get(k).getName());
                                }
                            }
                        }

                    }else{
                        Toast.makeText(SupplierListActivity.this, response.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                        if (response.getString("ret_code").equals("0011")){
                            Intent intent=new Intent(SupplierListActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogUtil.e("SupplierListActivity", "sign in fail:" + throwable.getMessage()+"--"+responseString);
                loadingLayout.setVisibility(View.VISIBLE);
                loadingBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.e("SupplierListActivity", "fail:" + throwable.getMessage());
                loadingLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                moreTv.setVisibility(View.GONE);
                loadingBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_workorder:
                    Intent i4 = new Intent(mContext, first_workorder_activity.class);
                    i4.putExtra("state","001");
                    startActivity(i4);
                    finish();
                    break;
                case R.id.iv_message:
                    Intent i5 = new Intent(mContext, MainActivity.class);
                    startActivity(i5);
                    finish();
                    break;
                case R.id.iv_me:{
                    Intent intent = new Intent(mContext,MeProfileActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
            }
        }
    };
}
