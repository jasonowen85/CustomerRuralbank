package com.grgbanking.ruralbank.main.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.workOrder;
import com.grgbanking.ruralbank.common.util.sys.ImageUtils;
import com.grgbanking.ruralbank.common.util.widget.ListViewCompat;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.activity.first_workorder_activity;
import com.grgbanking.ruralbank.main.activity.input_courier_number_activity;
import com.grgbanking.ruralbank.main.activity.input_evaluate_activity;
import com.grgbanking.ruralbank.main.activity.input_order_details_activity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/8/16.
 */
public class input_workorder_baskfragment extends BaseFragment implements
        ListViewCompat.OnRefreshListener, ListViewCompat.OnLoadListener {

    private ListViewCompat listView1;
    private ListAdapt mListAdapt;
    List<workOrder> datas;
    private int allCount = 200;
    private final static String MAINTENANCE = "001";//待维修
    private final static String CONFIRMED = "003";//待确认
    private final static String EVALUATION = "004";//待评价
    private final static String HISTORY = "005";//历史工单

    private String mType;
    private ReceiveBroadCast receiveBroadCast;
    private String startTime;
    private String endTime;
    private String lastDeviceTypeId;
    private String lastSupplierId;
    private int currentPage = 1;

    @Override
    public void onAttach(Activity activity) {
        //注册广播
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_NAME");    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_input_maintenance, container, false);
        datas = new ArrayList<workOrder>();
        if (lastDeviceTypeId == null || lastSupplierId == null || endTime == null || startTime == null) {
            getData(ListViewCompat.REFRESH);
        }
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(ListViewCompat.REFRESH);
    }

    class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startTime = intent.getExtras().getString("startTime");
            endTime = intent.getExtras().getString("endTime");
            lastDeviceTypeId = intent.getExtras().getString("deviceTypeId");
            lastSupplierId = intent.getExtras().getString("supplierId");
            if (lastDeviceTypeId != null) {
                NimApplication.lastDeviceTypeId = lastDeviceTypeId;
            }
            if (lastSupplierId != null) {
                NimApplication.lastSupplierId = lastSupplierId;
            }
            if (startTime != null || endTime != null) {
                NimApplication.lastEndtime = endTime;
                NimApplication.lastStarttime = startTime;
            }
            getData(ListViewCompat.REFRESH);
        }
    }

    private String Return(String str) {
        if (str == null) {
            return "0000";
        }
        return str;
    }

    /*类型 001 待维修，002 进行中，003 待确认，004 待评价，005 历史工单*/
    protected void SetType(String type) {
        this.mType = type;
    }

    protected void init(View view) {
        listView1 = (ListViewCompat) view.findViewById(R.id.listView1);
        mListAdapt = new ListAdapt(getActivity());
        listView1.setAdapter(mListAdapt);
        listView1.setOnRefreshListener(this);
        listView1.setOnLoadListener(this);
        listView1.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (datas.size() == 0) {
                            return;
                        }
                        if (view.getId() != R.id.iv_action1 && view.getId() != R.id.iv_action2) {
                            if (parent.getAdapter().getItem(position) == null) {
                                return;
                            }
                            Intent it = new Intent(getActivity(), input_order_details_activity.class);
                            it.putExtra("orderId", datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getId());
                            startActivity(it);
                        }
                    }
                }
        );
        ToolUtil.ReCalListViewHeightBasedOnChildren(listView1);
    }

    protected void getData(final int what) {
        if(what == ListViewCompat.REFRESH  && currentPage != 1){
            //如果刷新数据 currentPage  = 1;
            currentPage =1;
        }
        ServerApi.jobOrderList(Preferences.getUserid(), currentPage, 10, mType, Return(NimApplication.lastDeviceTypeId), Return(NimApplication.lastSupplierId), Return(NimApplication.lastStarttime), Return(NimApplication.lastEndtime), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    JSONObject jsonObject = response.optJSONObject("lists");
                    JSONArray jsonArr = jsonObject.optJSONArray("lists");
                    int totalItem = jsonObject.optInt("total");
                    allCount = totalItem;
                    int totalPager = totalItem % 10 == 0 ? totalItem / 10 : totalItem / 10 + 1;
                    List<workOrder> orders = new ArrayList<workOrder>();
                    for (int i = 0; i < jsonArr.length(); i++) {
                        workOrder order = new workOrder();
                        JSONObject jsonOb = new JSONObject();
                        try {
                            jsonOb = jsonArr.getJSONObject(i);
                            order.setId(jsonOb.getString("id"));//iD
                            order.setDeviceName(jsonOb.getString("deviceName"));//设备名
                            order.setSchedule(jsonOb.getString("schedule"));//工单进度操作
                            order.setSituation(jsonOb.getString("situation"));//故障情况
                            order.setScheduleStr(jsonOb.getString("scheduleStr"));//工单进度名称
                            order.setCreateTime(jsonOb.getString("createTime"));//时间
                            order.setJobOrderType(jsonOb.getString("jobOrderType"));//类型
                            if (jsonOb.has("express")) {
                                order.setExpress(jsonOb.getString("express"));//快递公司
                                order.setCourierNum(jsonOb.getString("courierNum"));//快递单号
                            }

                            if (jsonOb.has("deviceNum")) {
                                order.setDeviceNum(jsonOb.getString("deviceNum"));//数量
                            }
                            if (jsonOb.has("imgSerialNum")) {
                                String picUrls = jsonOb.getString("imgSerialNum");
                                String[] arrs = picUrls.split(",");
                                for (String url : arrs) {
                                    if (!StringUtil.isEmpty(url)) {
                                        order.getImageUrls().add(url);
                                    }
                                }
                            }
                            orders.add(order);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ListViewCompat.REFRESH == what) {
                        datas.clear();
                        datas.addAll(orders);
                        listView1.onRefreshComplete();
                    } else if (ListViewCompat.LOAD == what) {
                        datas.addAll(orders);
                        listView1.onLoadComplete();
                    }
                    LogUtil.i("jiang", "当前请求页 = " + currentPage + "总datas size=" + datas.size() +
                            "   总totalItem = " + totalItem + "  本次请求获取到的数据条数= " + orders.size() + "  总页数= " + totalPager);
                    if (totalPager == currentPage) {
                        listView1.setNoNextPagerDatas();
                    } else {
                        if (totalPager > 1) {
                            currentPage++;
                        }
                        listView1.setResultSize(orders.size());
                    }
                    mListAdapt.notifyDataSetChanged();
                    orders.clear();
                } else {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                listView1.onLoadComplete();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                listView1.onLoadComplete();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                }
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
                    Toast.makeText(getActivity(), "操作成功！", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), first_workorder_activity.class);
                    i.putExtra("state", "004");
                    startActivity(i);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
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

    /*关闭工单*/
    protected void closedOrder(String orderid) {
        ServerApi.closeOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "关闭成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                        intent.putExtra("state", "001");
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
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


    /**撤单确认dialog
     * @param orderid
     */
    protected void confirmCancelOrder(final String orderid) {
        Activity activity = getActivity();
        EasyAlertDialogHelper.createOkCancelDiolag(activity, activity.getString(R.string.helps), activity.getString(R.string.confirm_cancle_order),
                activity.getString(R.string.cancle_order), activity.getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                        //什么都不干
                    }

                    @Override
                    public void doOkAction() {
                        cancelOrder(orderid);
                    }
                }).show();
    }

    /*撤单*/
    protected void cancelOrder(String orderid) {
        ServerApi.cancelOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "撤单成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                        intent.putExtra("state", "001");
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else if (ret_code.equals("100")) {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), first_workorder_activity.class);
                        intent.putExtra("state", "001");
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), ret_msg, Toast.LENGTH_SHORT).show();
                        if (ret_code.equals("0011")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
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

    @Override
    public void onLoad() {
        if (mListAdapt.getCount() < allCount) {
            getData(ListViewCompat.LOAD);
        } else {
            listView1.onLoadComplete();
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "已加载全部！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        getData(ListViewCompat.REFRESH);
    }

    class ViewHolder {
        TextView tv_schedule, tv_number, tv_deviceName, tv_situation, tv_createTime;
        ImageView img1, img2, img3;
        ImageView iv_action1, iv_action2;
    }

    class ListAdapt extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public ListAdapt(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                convertView = mLayoutInflater.inflate(R.layout.job_order_details_listview, null);

                vHolder.img1 = (ImageView) convertView.findViewById(R.id.img1);
                vHolder.img2 = (ImageView) convertView.findViewById(R.id.img2);
                vHolder.img3 = (ImageView) convertView.findViewById(R.id.img3);

                vHolder.tv_createTime = (TextView) convertView.findViewById(R.id.tv_createTime);
                vHolder.tv_situation = (TextView) convertView.findViewById(R.id.tv_situation);
                vHolder.tv_schedule = (TextView) convertView.findViewById(R.id.tv_schedule);
                vHolder.tv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
                vHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

                vHolder.iv_action1 = (ImageView) convertView.findViewById(R.id.iv_action1);
                vHolder.iv_action2 = (ImageView) convertView.findViewById(R.id.iv_action2);

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_deviceName.setText(datas.get(position).getDeviceName());
            vHolder.tv_situation.setText(datas.get(position).getSituation());
            vHolder.tv_schedule.setText(datas.get(position).getScheduleStr());
            vHolder.tv_number.setText(datas.get(position).getDeviceNum());
            vHolder.tv_createTime.setText(datas.get(position).getCreateTime());

            switch (datas.get(position).getImageUrls().size()){
                case 2:
                    vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    break;
                case 1 :
                    vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    vHolder.img2.setImageResource(R.drawable.nim_default_img_failed);
                    break;
                case 0 :
                    vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    vHolder.img2.setImageResource(R.drawable.nim_default_img_failed);
                    vHolder.img3.setImageResource(R.drawable.nim_default_img_failed);
                    break;

            }
            if (datas.get(position).getImageUrls().size() > 2) {
                if (Util.isOnMainThread()) {
                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(2))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img3);
                }
            }
            if (datas.get(position).getImageUrls().size() > 1) {
                if (Util.isOnMainThread()) {
                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(1))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img2);
                }
            }
            if (datas.get(position).getImageUrls().size() > 0) {
                if (Util.isOnMainThread()) {
                    Glide.with(input_workorder_baskfragment.this)
                            .load(String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(datas.get(position).getImageUrls().get(0))))
                            .crossFade()
                            .centerCrop()
                            .into(vHolder.img1);
                }
            }
            switch (mType) {
                case MAINTENANCE: //待维修001
                    if (datas.get(position).getSchedule().equals("1") || datas.get(position).getSchedule().equals("11") || datas.get(position).getSchedule().equals("12") || datas.get(position).getSchedule().equals("2")) { //   ---  action1 撤单， action2 录入快递号
                        vHolder.iv_action1.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setImageResource(R.drawable.button1);
                        if (datas.get(position).getJobOrderType().equals("2")) {
                            if (datas.get(position).getExpress() == null || datas.get(position).getCourierNum() == null || datas.get(position).getExpress().equals("") || datas.get(position).getCourierNum().equals("")) {
                                vHolder.iv_action2.setVisibility(View.VISIBLE);
                                vHolder.iv_action2.setImageResource(R.drawable.button9);
                            } else {
                                vHolder.iv_action2.setVisibility(View.GONE);
                            }
                        } else {
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                        vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmCancelOrder(datas.get(position).getId());
                            }
                        });
                        vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(getActivity(), input_courier_number_activity.class);
                                it.putExtra("jobOrderId", datas.get(position).getId());
                                startActivity(it);
                            }
                        });
                    } else if (datas.get(position).getSchedule().equals("3") || datas.get(position).getSchedule().equals("4")) { //   --- 撤单
                        vHolder.iv_action1.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setImageResource(R.drawable.button1);
                        vHolder.iv_action2.setVisibility(View.GONE);
                        vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmCancelOrder(datas.get(position).getId());
                            }
                        });
                    } else {
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                    break;
                case CONFIRMED: //待确认003
                    if (datas.get(position).getSchedule().equals("16")) { //   ---  确认收货
                        vHolder.iv_action2.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setImageResource(R.drawable.button14);

                        vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(datas.get(position).getId());
                            }
                        });
                    } else if (datas.get(position).getSchedule().equals("7")) { //--- 确认完成，
                        vHolder.iv_action1.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setImageResource(R.drawable.button12);
                        vHolder.iv_action2.setVisibility(View.GONE);

                        vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comfirmOrder(datas.get(position).getId());
                            }
                        });
                    } else if (datas.get(position).getSchedule().equals("5")) { //--- 撤单
                        vHolder.iv_action1.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setImageResource(R.drawable.button1);
                        vHolder.iv_action2.setVisibility(View.GONE);

                        vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmCancelOrder(datas.get(position).getId());
                            }
                        });
                    } else if (datas.get(position).getSchedule().equals("13")) {
                        vHolder.iv_action1.setVisibility(View.VISIBLE);
                        vHolder.iv_action1.setImageResource(R.drawable.button1);
                        if (datas.get(position).getJobOrderType().equals("2")) {
                            if (datas.get(position).getExpress() == null || datas.get(position).getCourierNum() == null || datas.get(position).getExpress().equals("") || datas.get(position).getCourierNum().equals("")) {
                                vHolder.iv_action2.setVisibility(View.VISIBLE);
                                vHolder.iv_action2.setImageResource(R.drawable.button9);
                            } else {
                                vHolder.iv_action2.setVisibility(View.GONE);
                            }
                        } else {
                            vHolder.iv_action2.setVisibility(View.GONE);
                        }
                        vHolder.iv_action1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EasyAlertDialogHelper.createOkCancelDiolag(mContext, mContext.getString(R.string.helps), mContext.getString(R.string.confirm_close_order),
                                        mContext.getString(R.string.close_order),mContext.getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                                            @Override
                                            public void doCancelAction() {
                                                //什么都不干
                                            }

                                            @Override
                                            public void doOkAction() {
                                                closedOrder(datas.get(position).getId());
                                            }
                                        }).show();
                            }
                        });
                        vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(getActivity(), input_courier_number_activity.class);
                                it.putExtra("jobOrderId", datas.get(position).getId());
                                startActivity(it);

                            }
                        });
                    } else {
                        vHolder.iv_action1.setVisibility(View.GONE);
                        vHolder.iv_action2.setVisibility(View.GONE);
                    }
                    break;
                case EVALUATION: //待评价004
                    vHolder.iv_action1.setVisibility(View.GONE);
                    vHolder.iv_action2.setVisibility(View.VISIBLE);
                    vHolder.iv_action2.setImageResource(R.drawable.button15);
                    vHolder.iv_action2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳到评价页面
                            Intent intent = new Intent(getActivity(), input_evaluate_activity.class);
                            intent.putExtra("jobOrderId", datas.get(position).getId());
                            startActivity(intent);
                        }
                    });
                    break;
                case HISTORY://历史工单005
                    vHolder.iv_action1.setVisibility(View.GONE);
                    vHolder.iv_action2.setVisibility(View.GONE);
                    break;
            }
            return convertView;
        }
    }

    private void callPhone(String phone){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    /**
     * 注销广播
     */
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(receiveBroadCast);
        super.onDestroyView();
    }

}
