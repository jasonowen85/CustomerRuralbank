package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.workOrder;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/8/18.
 */
public class WorkorderSearchActivity extends UI{
    private ListView lvOrders;
    private ListAdapt mListAdapt;
    private SearchView searchView;
    private List<workOrder> datas;
    workOrder order;
    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WorkorderSearchActivity.class);
        context.startActivity(intent);
    }
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.global_search_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                MenuItemCompat.expandActionView(item);
            }
        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                finish();
                return false;
            }
        });
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                showKeyboard(false);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String arg0) {
                if (StringUtil.isEmpty(arg0)) {
                    lvOrders.setVisibility(View.GONE);
                } else {
                    lvOrders.setVisibility(View.VISIBLE);
                    getDate(arg0);
                }
                return true;
            }
        });
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_search_result);
        datas = new ArrayList<workOrder>();

        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options);

        initID();
        findViewById(R.id.global_search_root).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finish();

                    return true;
                }
                return false;
            }
        });
    }
    private void initID() {
        lvOrders = (ListView) findViewById(R.id.searchResultList);
        mListAdapt=new ListAdapt(this);
        lvOrders.setAdapter(mListAdapt);
        lvOrders.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }
    //搜索
    private void getDate(String keyWord){
        ServerApi.getSearchJobOrder(1, 10, DemoCache.getUserid(), keyWord, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if(ret_code.equals("0")){
                    JSONObject jsonObject = response.optJSONObject("lists");
                    JSONArray jsonArr = jsonObject.optJSONArray("lists");
                    datas.clear();
                    for (int i=0; i < jsonArr.length(); i++) {
                        workOrder order = new workOrder();
                        JSONObject jsonOb = new JSONObject();
                        try {
                            jsonOb = jsonArr.getJSONObject(i);
                            order.setId(jsonOb.getString("id"));//ID
                            order.setDeviceName(jsonOb.getString("deviceName"));//设备名
                            order.setSchedule(jsonOb.getString("schedule"));//工单进度操作
                            order.setScheduleStr(jsonOb.getString("scheduleStr"));//工单进度名称
                            order.setSituation(jsonOb.getString("situation"));//故障情况
                            order.setCreateTime(jsonOb.getString("createTime"));//工单创建时间
                            order.setJobOrderType(jsonOb.getString("jobOrderType"));
                            String picUrls = jsonOb.getString("imgSerialNum");//图片路径集合
                            String[] arrs = picUrls.split(",");
                            for (String url : arrs) {
                                order.getImageUrls().add(url);
                            }
                            datas.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mListAdapt.notifyDataSetChanged();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(WorkorderSearchActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")){
                        Intent intent=new Intent(WorkorderSearchActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*关闭工单*/
    protected void closedOrder(String orderid) {
        ServerApi.closeOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(WorkorderSearchActivity.this, "关闭成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkorderSearchActivity.this, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(WorkorderSearchActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(WorkorderSearchActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*撤单*/
    protected void cancelOrder(String orderid) {
        ServerApi.cancelOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(WorkorderSearchActivity.this, "撤单成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkorderSearchActivity.this, first_workorder_activity.class);
                    intent.putExtra("state","001");
                    startActivity(intent);
                    WorkorderSearchActivity.this.finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(WorkorderSearchActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(WorkorderSearchActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /* 2.2.7.确认收货*/
    protected void comfirmOrder(String jobOrder_id) {
        ServerApi.comfirmOrder(jobOrder_id, Preferences.getUserid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(WorkorderSearchActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(WorkorderSearchActivity.this, first_workorder_activity.class);
                    i.putExtra("state","004");
                    startActivity(i);
                    WorkorderSearchActivity.this.finish();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(WorkorderSearchActivity.this, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(WorkorderSearchActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(WorkorderSearchActivity.this, "获取数据异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ViewHolder {
        TextView tv_schedule, tv_number, tv_deviceName, tv_situation, tv_createTime;
        LinearLayout ll_item;
        ImageView img1, img2, img3;
        ImageView iv_action1, iv_action2;
    }
    class ListAdapt extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        public ListAdapt(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return datas.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder = null;
            if (convertView == null) {
                vHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(
                        R.layout.job_order_details_listview, null);
                vHolder.img1 = (ImageView) convertView.findViewById(R.id.img1);
                vHolder.img2 = (ImageView) convertView.findViewById(R.id.img2);
                vHolder.img3 = (ImageView) convertView.findViewById(R.id.img3);

                vHolder.tv_createTime = (TextView) convertView.findViewById(R.id.tv_createTime);
                vHolder.tv_situation = (TextView) convertView.findViewById(R.id.tv_situation);
                vHolder.tv_schedule = (TextView) convertView.findViewById(R.id.tv_schedule);
                vHolder.tv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
                vHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

                vHolder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
                vHolder.iv_action1 = (ImageView) convertView.findViewById(R.id.iv_action1);
                vHolder.iv_action2 = (ImageView) convertView.findViewById(R.id.iv_action2);

                vHolder.iv_action1.setImageResource(R.drawable.confirm_complete);
                vHolder.iv_action2.setImageResource(R.drawable.confirmation_delivery);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_deviceName.setText(datas.get(position).getDeviceName());
            vHolder.tv_situation.setText(datas.get(position).getSituation());
            vHolder.tv_schedule.setText(datas.get(position).getScheduleStr());
            vHolder.tv_createTime.setText(datas.get(position).getCreateTime());

            vHolder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(WorkorderSearchActivity.this, input_order_details_activity.class);
                    it.putExtra("orderId", datas.get(position).getId().toString());
                    startActivity(it);
                }
            });

            if(datas.get(position).getImageUrls().size()>2){
                Glide.with(WorkorderSearchActivity.this)
                        .load(String.format(ApiHttpClient.API_URL_IMG, datas.get(position).getImageUrls().get(2)))
                        .override(120, 120)
                        .into(vHolder.img3);
            }
            if(datas.get(position).getImageUrls().size()>1){
                Glide.with(WorkorderSearchActivity.this)
                        .load(String.format(ApiHttpClient.API_URL_IMG, datas.get(position).getImageUrls().get(1)))
                        .override(120, 120)
                        .into(vHolder.img2);
            }
            if(datas.get(position).getImageUrls().size()>0){
                Glide.with(WorkorderSearchActivity.this)
                        .load(String.format(ApiHttpClient.API_URL_IMG, datas.get(position).getImageUrls().get(0)))
                        .override(120, 120)
                        .into(vHolder.img1);
            }

            if (datas.get(position).getSchedule().equals("1") || datas.get(position).getSchedule().equals("11") ||datas.get(position).getSchedule().equals("12") || datas.get(position).getSchedule().equals("2")) { //   ---  action1 撤单， action2 录入快递号
                vHolder.iv_action1.setVisibility(View.VISIBLE);
                vHolder.iv_action1.setImageResource(R.drawable.button1);
                if (datas.get(position).getJobOrderType().equals("2")){
                    if (datas.get(position).getExpress()==null || datas.get(position).getCourierNum()==null|| datas.get(position).getExpress().equals("")||datas.get(position).getCourierNum().equals("")) {
                        vHolder.iv_action2.setVisibility(View.VISIBLE);
                        vHolder.iv_action2.setImageResource(R.drawable.button9);
                    } else {
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                }else{
                    vHolder.iv_action2.setVisibility(View.GONE);
                }
                vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(datas.get(position).getId());
                    }
                });
                vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(WorkorderSearchActivity.this, input_courier_number_activity.class);
                        it.putExtra("jobOrderId", datas.get(position).getId());
                        startActivity(it);
                    }
                });
            } else if ( datas.get(position).getSchedule().equals("3") || datas.get(position).getSchedule().equals("4")) { //   --- 撤单
                vHolder.iv_action1.setVisibility(View.VISIBLE);
                vHolder.iv_action1.setImageResource(R.drawable.button1);
                vHolder.iv_action2.setVisibility(View.GONE);
                vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(datas.get(position).getId());
                    }
                });
            } else  if (datas.get(position).getSchedule().equals("16")) { //   ---  确认收货
                vHolder.iv_action1.setVisibility(View.GONE);
                vHolder.iv_action2.setVisibility(View.VISIBLE);
                vHolder.iv_action2.setImageResource(R.drawable.button14);

                vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comfirmOrder(datas.get(position).getId());
                    }
                });
            } else if (datas.get(position).getSchedule().equals("7")) { //--- 确认完成，
                vHolder.iv_action1.setImageResource(R.drawable.button12);
                vHolder.iv_action1.setVisibility(View.VISIBLE);
                vHolder.iv_action2.setVisibility(View.GONE);

                vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comfirmOrder(datas.get(position).getId());
                    }
                });
            } else if (datas.get(position).getSchedule().equals("5") ) { //--- 撤单
                vHolder.iv_action1.setImageResource(R.drawable.button1);
                vHolder.iv_action1.setVisibility(View.VISIBLE);
                vHolder.iv_action2.setVisibility(View.GONE);

                vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(datas.get(position).getId());
                    }
                });
            } else if (datas.get(position).getSchedule().equals("13") ) { //--- 撤单 录入快递号
                vHolder.iv_action1.setImageResource(R.drawable.button1);
                vHolder.iv_action1.setVisibility(View.VISIBLE);
                if (datas.get(position).getJobOrderType().equals("2")){
                    if (datas.get(position).getExpress()==null || datas.get(position).getCourierNum()==null|| datas.get(position).getExpress().equals("")||datas.get(position).getCourierNum().equals("")) {
                        vHolder.iv_action2.setVisibility(View.VISIBLE);
                        vHolder.iv_action2.setImageResource(R.drawable.button9);
                    } else {
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                }else{
                    vHolder.iv_action2.setVisibility(View.GONE);
                }
                vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder(datas.get(position).getId());
                    }
                });
                vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(WorkorderSearchActivity.this, input_courier_number_activity.class);
                        it.putExtra("jobOrderId", datas.get(position).getId());
                        startActivity(it);
                    }
                });
            }else if (datas.get(position).getSchedule().equals("8") || datas.get(position).getSchedule().equals("17")){
                vHolder.iv_action1.setVisibility(View.GONE);
                vHolder.iv_action2.setVisibility(View.VISIBLE);
                vHolder.iv_action2.setImageResource(R.drawable.button15);
                vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳到评价页面
                        Intent intent = new Intent(WorkorderSearchActivity.this, input_evaluate_activity.class);
                        intent.putExtra("jobOrderId", datas.get(position).getId());
                        startActivity(intent);
                    }
                });
            }else{
                vHolder.iv_action1.setVisibility(View.GONE);
                vHolder.iv_action2.setVisibility(View.GONE);
            }
                return convertView;
        }

    }
}
