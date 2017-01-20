package com.grgbanking.ruralbank.main.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.Spinner.AbstractSpinerAdapter;
import com.grgbanking.ruralbank.main.Spinner.CustemObject;
import com.grgbanking.ruralbank.main.Spinner.CustemSpinerAdapter;
import com.grgbanking.ruralbank.main.Spinner.SpinerPopWindow;
import com.grgbanking.ruralbank.main.model.AddworderBean;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class dropbox_branch_fragment extends BaseFragment implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener {
    public TextView tv_company;
    private ImageView iv_company;
    private List<CustemObject> nameList = new ArrayList<CustemObject>();
    private AbstractSpinerAdapter mAdapter;
    private LinearLayout ll_company_out;
    private List<AddworderBean> beanList;
    private ReceiveBroadCast receiveBroadCast;
    String type;
    private List<AddworderBean> worderBeen;

    @Override
    public void onAttach(Activity activity) {
        //注册广播
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("COMPANY_NAME");    //只有持有相同的action的接受者才能接收此广播
       if(getActivity()!=null) {
           getActivity().registerReceiver(receiveBroadCast, filter);
       }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dropbox_branch, container, false);
        init(rootView);
        return rootView;
    }

    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            nameList.clear();
            type = null;
            type = intent.getExtras().getString("id");
            getSuppliers(type);
            theAssignment();
        }
    }

    private void getSuppliers(String type) {
        ServerApi.getSuppliers(type, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    JSONArray jsonArr = response.optJSONArray("lists");
                    worderBeen = new ArrayList<AddworderBean>();
                    AddworderBean order = new AddworderBean();
                    List<String> names = new ArrayList<String>();
                    List<String> ids = new ArrayList<String>();
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonOb = new JSONObject();
                        try {
                            jsonOb = jsonArr.getJSONObject(i);
                            order.setEquipmentId(jsonOb.getString("id"));
                            order.setEquipment(jsonOb.getString("name"));
                            worderBeen.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ids.add(order.getEquipmentId());
                        names.add(order.getEquipment());
                    }
                    CustemObject object1 = new CustemObject();
                    object1.data = "全部";
                    nameList.add(object1);
                    for (int i = 0; i < names.size(); i++) {
                        CustemObject object = new CustemObject();
                        object.data = names.get(i);
                        object.id = ids.get(i);
                        nameList.add(object);
                    }
                    theAssignment();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init(View rootView) {
        ll_company_out = (LinearLayout) rootView.findViewById(R.id.ll_company_out);
        ll_company_out.setOnClickListener(this);
        tv_company = (TextView) rootView.findViewById(R.id.tv_company);
        tv_company.setOnClickListener(this);
        iv_company = (ImageView) rootView.findViewById(R.id.iv_company);
        iv_company.setOnClickListener(this);
        theAssignment();
    }

    private void theAssignment() {
        mAdapter = new CustemSpinerAdapter(getActivity());
        mAdapter.refreshData(nameList, 0);
        mSpinerPopWindow = new SpinerPopWindow(getActivity());
        mSpinerPopWindow.setAdatper(mAdapter);
        mSpinerPopWindow.setItemListener(this);
    }

    private SpinerPopWindow mSpinerPopWindow;

    private void showSpinWindow() {
        Log.e("", "showSpinWindow");
        mSpinerPopWindow.setWidth(ll_company_out.getWidth());
        mSpinerPopWindow.showAsDropDown(ll_company_out);
    }

    @Override
    public void onItemClick(int pos) {
        setHero(pos);
    }

    private void setHero(int pos) {
        CustemObject value = new CustemObject();
        if (pos >= 0 && pos <= nameList.size()) {
            value = nameList.get(pos);
            tv_company.setText(value.toString());

            Intent intent = new Intent();
            intent.setAction("ACTION_NAME");
            if (tv_company.getText().equals("全部")) {
                intent.putExtra("supplierId", "0000");
            } else {
                intent.putExtra("supplierId", value.idString());
            }
            if (getActivity() != null) {
                getActivity().sendBroadcast(intent);//发送广播
            }
        }
    }

    @Override
    public void onClick(View v) {
        showSpinWindow();
    }

    /**
     * 注销广播
     */
    @Override
    public void onDestroyView() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(receiveBroadCast);
        }
        super.onDestroyView();
    }
}
