package com.grgbanking.ruralbank.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.tracking;
import com.grgbanking.ruralbank.common.bean.workOrder;
import com.grgbanking.ruralbank.common.util.widget.TimeLineView;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.activity.LocusActivity;
import com.grgbanking.ruralbank.session.SessionHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderStateFragment extends Fragment {

    private Context mContext;
    private View view;
    private String mOrderId;
//    private ImageView iv_action2, iv_action1;
    private List<tracking> datas;
    private TimeLineView mTimeLineView;
    private ListAdapt mListAdapt;
    private ListView mListView;
//    private ImageView star1, star2, star3, star4, star5;
    private workOrder mWorkOrder;
    private String voiceUrl;
    private int mOrderType = 0;//1 上门维修   2 寄件返修
//    private ImageView[] iv_picturecompletes = new ImageView[9];
    private LinearLayout ll_express, ll_evaluate, ll_complete;
    private LinearLayout ll_workorder_tracking, ll_contact_address;
    private TextView tv_complete, tv_line, tv_contact_phone, tv_therepair_name, tv_contact_address, tv_express, tv_courierNum, tv_evaluate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_state, container, false);
        mContext = getActivity();
        initView();
        initData();
        return view;
    }

    private void initView() {
        //tweet_layout_record = view.findViewById(R.id.tweet_layout_record);
//        iv_action2 = (ImageView) view.findViewById(R.id.iv_action2);
//        iv_action1 = (ImageView) view.findViewById(R.id.iv_action1);
        mTimeLineView = (TimeLineView) view.findViewById(R.id.tl_tracking_step);
        mListView = (ListView) view.findViewById(R.id.list_tracking);
//        star1 = (ImageView) view.findViewById(R.id.star1);
//        star2 = (ImageView) view.findViewById(R.id.star2);
//        star3 = (ImageView) view.findViewById(R.id.star3);
//        star4 = (ImageView) view.findViewById(R.id.star4);
//        star5 = (ImageView) view.findViewById(R.id.star5);
//        tv_express = (TextView) view.findViewById(R.id.tv_express);
//        tv_courierNum = (TextView) view.findViewById(R.id.tv_courierNum);
//        ll_express = (LinearLayout) view.findViewById(R.id.ll_express);
//        ll_complete = (LinearLayout) view.findViewById(R.id.ll_complete);
//        ll_evaluate = (LinearLayout) view.findViewById(R.id.ll_evaluate);
//        tv_evaluate = (TextView) view.findViewById(R.id.tv_evaluate);
//        tv_complete = (TextView) view.findViewById(R.id.tv_complete);
//        //tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
//        tv_therepair_name = (TextView) view.findViewById(R.id.tv_therepair_name);//维修人
//        tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);//维修人电话
//        ll_workorder_tracking = (LinearLayout) view.findViewById(R.id.ll_workorder_tracking);
//
//        tv_line = (TextView) view.findViewById(R.id.tv_line);
//        ll_contact_address = (LinearLayout) view.findViewById(R.id.ll_contact_address);
//        tv_contact_address = (TextView) view.findViewById(R.id.tv_contact_address);//地址

    }

    private void initData() {
        mOrderId = getActivity().getIntent().getStringExtra("mOrderId");
        LogUtil.e("OrderStateFragment","mOrderId == " + mOrderId);
        datas = new ArrayList<>();
        mListAdapt = new ListAdapt(mContext);
        mListView.setAdapter(mListAdapt);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getState().equals("14")){
                    Intent intent = new Intent(mContext,LocusActivity.class);
                    intent.putExtra("jobOrderId", mOrderId);
                    startActivity(intent);
                }else{
                    SessionHelper.startP2PSession(mContext, datas.get(Integer.parseInt(parent.getAdapter().getItem(position).toString())).getContactId());
                }
            }
        });
        getTrackingData();
        //getOrderData();
    }

//    private void setButtons( String schedule) {
//        if ( schedule.equals("3") || schedule.equals("4")) { //   --- 撤单
//            iv_action1.setVisibility(View.VISIBLE);
//            iv_action1.setImageResource(R.drawable.button1);
//            iv_action1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cancelOrder(mOrderId);
//                    iv_action1.setClickable(false);
//                }
//            });
//        } else if (schedule.equals("11") || schedule.equals("1") ||schedule.equals("12") || schedule.equals("2")) {//撤单、录入快递单号
//            iv_action1.setVisibility(View.VISIBLE);
//            iv_action1.setImageResource(R.drawable.button1);
//            if(mOrderType==2){
//                if (mWorkOrder.getExpress() == null || mWorkOrder.getCourierNum() == null || mWorkOrder.getExpress().equals("") || mWorkOrder.getCourierNum().equals("")) {
//                    iv_action2.setVisibility(View.VISIBLE);
//                    iv_action2.setImageResource(R.drawable.button9);
//                }
//            }
//            iv_action1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cancelOrder(mOrderId);
//                    iv_action1.setClickable(false);
//                }
//            });
//            iv_action2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent it = new Intent(mContext, input_courier_number_activity.class);
//                    it.putExtra("jobOrderId", mOrderId);
//                    startActivity(it);
//                    iv_action2.setClickable(false);
//
//                }
//            });
//        }else if (schedule.equals("16")) { //   ---   确认收货
//            iv_action2.setVisibility(View.VISIBLE);
//            iv_action2.setImageResource(R.drawable.button14);
//
//            iv_action2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    comfirmOrder(mOrderId);
//                    iv_action2.setClickable(false);
//                }
//            });
//        } else if (schedule.equals("7")) { //---  确认完成
//            iv_action1.setVisibility(View.VISIBLE);
//            iv_action1.setImageResource(R.drawable.button12);
//            iv_action1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    comfirmOrder(mOrderId);
//                    iv_action1.setClickable(false);
//                }
//            });
//        } else if (schedule.equals("5") ) { //--- 撤单
//            iv_action1.setVisibility(View.VISIBLE);
//            iv_action1.setImageResource(R.drawable.button1);
//            iv_action1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cancelOrder(mOrderId);
//                    iv_action1.setClickable(false);
//                }
//            });
//
//        }else if (schedule.equals("13")) { //--- 撤单 录入快递号
//            iv_action1.setVisibility(View.VISIBLE);
//            iv_action1.setImageResource(R.drawable.button1);
//            if(mOrderType==2){
//                if (mWorkOrder.getExpress() == null || mWorkOrder.getCourierNum() == null || mWorkOrder.getExpress().equals("") || mWorkOrder.getCourierNum().equals("")) {
//                    iv_action2.setVisibility(View.VISIBLE);
//                    iv_action2.setImageResource(R.drawable.button9);
//                }
//            }
//            iv_action1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    closedOrder(mOrderId);
//                    iv_action1.setClickable(false);
//                }
//            });
//            iv_action2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent it = new Intent(mContext, input_courier_number_activity.class);
//                    it.putExtra("jobOrderId", mOrderId);
//                    startActivity(it);
//                    iv_action2.setClickable(false);
//                }
//            });
//        }else if(schedule.equals("8")||schedule.equals("17")){
//            iv_action2.setVisibility(View.VISIBLE);
//            iv_action2.setImageResource(R.drawable.button15);
//            iv_action2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //跳到评价页面
//                    Intent intent = new Intent(mContext, input_evaluate_activity.class);
//                    intent.putExtra("jobOrderId", mOrderId);
//                    startActivity(intent);
//                    iv_action2.setClickable(false);
//                }
//            });
//        }
//
//    }
//
//    /* 2.2.7.确认收货*/
//    protected void comfirmOrder(String jobOrder_id) {
//        ServerApi.comfirmOrder(jobOrder_id, Preferences.getUserid(), new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                String ret_code = response.optString("ret_code");
//                if (ret_code.equals("0")) {
//                    Toast.makeText(mContext, "接单或者确认成功！", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(mContext, first_workorder_activity.class);
//                    i.putExtra("state", "004");
//                    startActivity(i);
//                    getActivity().finish();
//                } else {
//                    String ret_msg = response.optString("ret_msg");
//                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
//                    if (ret_code.equals("0011")) {
//                        Intent intent = new Intent(mContext, LoginActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /*撤单*/
//    protected void cancelOrder(String orderid) {
//        ServerApi.cancelOrder(orderid, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                String ret_code = response.optString("ret_code");
//                if (ret_code.equals("0")) {
//                    Toast.makeText(mContext, "撤单成功！", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(mContext, first_workorder_activity.class);
//                    intent.putExtra("state", "001");
//                    startActivity(intent);
//                    getActivity().finish();
//                } else if(ret_code.equals("100")){
//                    String ret_msg = response.optString("ret_msg");
//                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(mContext, first_workorder_activity.class);
//                    intent.putExtra("state", "001");
//                    startActivity(intent);
//                    getActivity().finish();
//                }else {
//                    String ret_msg = response.optString("ret_msg");
//                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
//                    if (ret_code.equals("0011")) {
//                        Intent intent = new Intent(mContext, LoginActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /*关闭工单*/
//    protected void closedOrder(String orderid) {
//        ServerApi.closeOrder(orderid, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                String ret_code = response.optString("ret_code");
//                if (ret_code.equals("0")) {
//                    Toast.makeText(mContext, "关闭成功！", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(mContext, first_workorder_activity.class);
//                    intent.putExtra("state", "001");
//                    startActivity(intent);
//                    getActivity().finish();
//                } else {
//                    String ret_msg = response.optString("ret_msg");
//                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
//                    if (ret_code.equals("0011")) {
//                        Intent intent = new Intent(mContext, LoginActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    }
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void getOrderData() {
//        ServerApi.getJobOrderDetails(mOrderId, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                String ret_code = response.optString("ret_code");
//
//                if (ret_code.equals("0")) {
//                    String json = response.toString();
//                    LogUtil.e("SupplierListActivity", "ret_code==" + json);
//
//                    JSONObject jsonOb = response.optJSONObject("lists");
//                    JSONObject jsonObj = jsonOb.optJSONObject("lists");
//                    JSONObject jsonObj2 = jsonOb.optJSONObject("jobOrderDetail");
//                    try {
//                        mWorkOrder = new workOrder();
//                        mWorkOrder.setJobNum(jsonObj.getString("jobNum"));//工单号
//                        mWorkOrder.setBankName(jsonObj.getString("bankName"));
//                        if (jsonObj2.has("courierNum") && !StringUtil.isEmpty(jsonObj2.getString("courierNum"))) {
//                            tv_express.setText(jsonObj2.getString("express"));//快递公司
//                            tv_courierNum.setText(jsonObj2.getString("courierNum"));//快递单号
//                            mWorkOrder.setExpress(jsonObj2.getString("express"));
//                            mWorkOrder.setCourierNum(jsonObj2.getString("courierNum"));
//                            ll_express.setVisibility(View.VISIBLE);
//                        }
//                        if (jsonObj2.has("evaluate")) {
//                            tv_evaluate.setText(jsonObj2.getString("evaluate"));//工单号
//                            switch (jsonObj2.getInt("starLevel")) {
//                                case 1:
//                                    star1.setImageResource(R.drawable.star2);
//                                    star2.setImageResource(R.drawable.star1);
//                                    star3.setImageResource(R.drawable.star1);
//                                    star4.setImageResource(R.drawable.star1);
//                                    star5.setImageResource(R.drawable.star1);
//                                    break;
//                                case 2:
//                                    star1.setImageResource(R.drawable.star2);
//                                    star2.setImageResource(R.drawable.star2);
//                                    star3.setImageResource(R.drawable.star1);
//                                    star4.setImageResource(R.drawable.star1);
//                                    star5.setImageResource(R.drawable.star1);
//                                    break;
//                                case 3:
//                                    star1.setImageResource(R.drawable.star2);
//                                    star2.setImageResource(R.drawable.star2);
//                                    star3.setImageResource(R.drawable.star2);
//                                    star4.setImageResource(R.drawable.star1);
//                                    star5.setImageResource(R.drawable.star1);
//                                    break;
//                                case 4:
//                                    star1.setImageResource(R.drawable.star2);
//                                    star2.setImageResource(R.drawable.star2);
//                                    star3.setImageResource(R.drawable.star2);
//                                    star4.setImageResource(R.drawable.star2);
//                                    star5.setImageResource(R.drawable.star1);
//                                    break;
//                                case 5:
//                                    star1.setImageResource(R.drawable.star2);
//                                    star2.setImageResource(R.drawable.star2);
//                                    star3.setImageResource(R.drawable.star2);
//                                    star4.setImageResource(R.drawable.star2);
//                                    star5.setImageResource(R.drawable.star2);
//                                    break;
//                            }
//
//                            ll_evaluate.setVisibility(View.VISIBLE);
//                        }
//
//                        String picUrls = jsonObj.getString("imgSerialNum");
//                        mWorkOrder.setSupName(jsonObj.getString("supName"));
//                        mWorkOrder.setDeviceName(jsonObj.getString("deviceName"));
//                        mWorkOrder.setDmName(jsonObj.getString("dmName"));
//                        mWorkOrder.setRemark(jsonObj.getString("remark"));
//                        mWorkOrder.setSituation("故障情况：  " + jsonObj.getString("situation"));
//                        mOrderType = jsonObj2.getInt("type");
//                        if (mOrderType == 1) {
//                            ll_contact_address.setVisibility(View.GONE);
//                            tv_line.setVisibility(View.GONE);
//                        }
//                        if (jsonObj2.has("comAddress")) {
//                            tv_contact_address.setText(jsonObj2.getString("comAddress"));
//                        }
//                        if (jsonObj.has("execution")) {
//                            ll_complete.setVisibility(View.VISIBLE);
//                            tv_complete.setText(jsonObj.getString("execution"));
//                        }
//                        if (jsonObj.has("imageStr")) {
//
//                            String picUrl2s = jsonObj.getString("imageStr");
//                            String[] arrs = picUrl2s.split(",");
//                            showPicture(arrs, iv_picturecompletes);
//                        }
//                        if (jsonObj2.has("voice")) {
//                            voiceUrl = jsonObj2.getString("voice");
//                            if(!voiceUrl.equals("")){
//                                //tweet_layout_record.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        String[] arrs = picUrls.split(",");
//                        //showPicture(arrs, iv_pictures);
//                        EventBus.getDefault().post(new EventLatLng(mWorkOrder,arrs,voiceUrl));
//                        tv_therepair_name.setText(jsonObj.getString("userName"));
//                        tv_contact_phone.setText(jsonObj.getString("phone"));
//                        String schedule = jsonObj.getString("schedule");
//                        String state = jsonObj.getString("state");
//                        setButtons( schedule);
//                        getTrackingData();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    String ret_msg = response.optString("ret_msg");
//                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
//                    if (ret_code.equals("0011")) {
//                        Intent intent = new Intent(mContext, LoginActivity.class);
//                        startActivity(intent);
//                        getActivity().finish();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //工单跟踪
    private void getTrackingData() {
        ServerApi.getWorkOrderTracking(mOrderId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    datas.clear();
                    JSONObject jsonOb = response.optJSONObject("lists");
                    JSONArray jsonArr = jsonOb.optJSONArray("lists");
                    LogUtil.e("input_order_details_activity",jsonArr.toString());
                    List<tracking> ts = new ArrayList<tracking>();
                    try {
                        for (int i = 0; i < jsonArr.length(); i++) {

                            tracking t = new tracking();
                            jsonOb = jsonArr.getJSONObject(i);
                            t.setSupplierSendUserId(jsonOb.getString("supplierSendUserId"));
                            t.setSendSupName(jsonOb.getString("sendSupName"));
                            t.setSupplierAcceptUserId(jsonOb.getString("supplierAcceptUserId"));
                            t.setAcceptSupName(jsonOb.getString("acceptSupName"));
                            t.setCreateTime(jsonOb.getString("createTime"));
                            t.setBankUserId(jsonOb.has("bankUserId") ? jsonOb.getString("bankUserId") : "");
                            t.setBankName(jsonOb.has("bankName") ? jsonOb.getString("bankName") : "");
                            t.setBankPhone(jsonOb.has("bankUserPhone") ? jsonOb.getString("bankUserPhone") : "");
                            t.setSupplierAcceptphone(jsonOb.has("supUserPhone") ? jsonOb.getString("supUserPhone") : "");
                            t.setState(jsonOb.getString("state"));
                            if (jsonOb.has("express") && !StringUtil.isEmpty(jsonOb.getString("express"))) {
                                t.setExpress(jsonOb.getString("express"));
                                t.setCourierNum(jsonOb.getString("courierNum"));//工单号
                            }
                            if (jsonOb.has("signAddress") && !StringUtil.isEmpty(jsonOb.getString("signAddress"))) {
                                t.setSignAddress(jsonOb.getString("signAddress"));
                            }
                            if (jsonOb.has("coordinates") && !StringUtil.isEmpty(jsonOb.getString("coordinates"))) {
                                t.setCoordinates(jsonOb.getString("coordinates"));
                            }
                            t.setContent(getTracktingStr(t));
                            ts.add(t);

                        }
                        datas.addAll(ts);
                        mTimeLineView.setTimelineCount(datas.size());
                        mListAdapt.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    if (ret_code.equals("0011")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject obj) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTracktingStr(tracking t) {
        if (t.getState().equals("1")) { //1.已填写工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step1), t.getBankName(), t.getAcceptSupName());
        } else if (t.getState().equals("2")) { //2.客服将工单转发给维修接口人
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step2), t.getSendSupName());
        } else if (t.getState().equals("3")) { //3.接口人接单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step3), t.getAcceptSupName());
        } else if (t.getState().equals("4")) { //4.接口人确认收货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step4), t.getAcceptSupName());
        } else if (t.getState().equals("5")) { //5.接口人确认维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step5), t.getAcceptSupName());
        } else if (t.getState().equals("6")) { //6.接口人确认发货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step6), t.getAcceptSupName());
        } else if (t.getState().equals("7")) { //7.客户确定到货
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step7), t.getAcceptSupName());
        } else if (t.getState().equals("8")) { //8.客户评价
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step8), t.getAcceptSupName());
        } else if (t.getState().equals("11")) { //11.已填写工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step11), t.getBankName(), t.getAcceptSupName());
        } else if (t.getState().equals("12")) { //12.服务主管接收工单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step12), t.getAcceptSupName());
        } else if (t.getState().equals("13")) { //13.服务主管下发给服务工程师
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step13), t.getAcceptSupName());
        } else if (t.getState().equals("14")) { //14.服务工程师接单
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step14), t.getAcceptSupName());
        } else if (t.getState().equals("15")) { //15.服务工程师已签到
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step15), t.getAcceptSupName());
        } else if (t.getState().equals("16")) { //16.服务工程师已维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step16), t.getAcceptSupName());
        } else if (t.getState().equals("17")) { //17.客户已确认维修完成
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step17), t.getAcceptSupName());
        } else if (t.getState().equals("18")) { //8.客户评价
            t.setContactId(t.getSupplierAcceptphone());
            return String.format(getResources().getString(R.string.workorder_step18), t.getAcceptSupName());
        }
        return "";
    }

    class ViewHolder {
        TextView tv_content, tv_express, tv_time,tv_SignAddress;
        MapView mv_map;
        View lineOne,lineTwo;
        ImageView ivHeadPoint;
    }

    class ListAdapt extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private BaiduMap mBaiduMap;

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
                convertView = mLayoutInflater.inflate(R.layout.job_order_tracking_listview, null);
                vHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                vHolder.tv_express = (TextView) convertView.findViewById(R.id.tv_express);
                vHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                vHolder.tv_SignAddress=(TextView)convertView.findViewById(R.id.tv_SignAddress);
                vHolder.mv_map = (MapView) convertView.findViewById(R.id.mv_map);
                vHolder.ivHeadPoint = (ImageView) convertView.findViewById(R.id.head_point);
                vHolder.lineOne = convertView.findViewById(R.id.head_line_one);
                vHolder.lineTwo = convertView.findViewById(R.id.head_line_two);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            vHolder.tv_time.setText(datas.get(position).getCreateTime());
            if (datas.get(position).getExpress() != null) {
                vHolder.tv_express.setVisibility(View.VISIBLE);
                vHolder.tv_express.setText("快递：" + datas.get(position).getExpress() + " " + datas.get(position).getCourierNum());
            }else{
                vHolder.tv_express.setVisibility(View.GONE);
            }
            //当进度为已接单，显示一个地图，然后点击进入看接单人的运动轨迹
            if (datas.get(position).getState().equals("14")){
                vHolder.mv_map.setVisibility(View.VISIBLE);
                mBaiduMap = vHolder.mv_map.getMap();
                if(datas.get(position).getCoordinates()!=null&&!datas.get(position).getCoordinates().equals("")){
                    String[] strings = datas.get(position).getCoordinates().split(",");
                    mBaiduMap.clear();
                    LatLng latLng = new LatLng(Double.valueOf(strings[0]),Double.valueOf(strings[1]));
                    OverlayOptions ooA = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.map_supplier))
                            .zIndex(4).draggable(false);
                    mBaiduMap.addOverlay(ooA);
                    MapStatus mMapStatus = new MapStatus.Builder()
                            .target(latLng)
                            .zoom(16)
                            .build();
                    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    //改变地图状态
                    mBaiduMap.setMapStatus(mMapStatusUpdate);
//                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 16.0f);
//                    mBaiduMap.animateMapStatus(u);
                }
                mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent intent = new Intent(mContext,LocusActivity.class);
                        intent.putExtra("jobOrderId", mOrderId);
                        startActivity(intent);
                    }

                    @Override
                    public boolean onMapPoiClick(MapPoi mapPoi) {
                        return false;
                    }
                });
            }else {
                vHolder.mv_map.setVisibility(View.GONE);
            }
            vHolder.mv_map.showScaleControl(false);
            vHolder.mv_map.showZoomControls(false);
            if (datas.get(position).getState().equals("15")){
                vHolder.tv_SignAddress.setVisibility(View.VISIBLE);
                vHolder.tv_SignAddress.setText(datas.get(position).getSignAddress());
            }else {
                vHolder.tv_SignAddress.setVisibility(View.GONE);
            }
            vHolder.tv_content.setText(Html.fromHtml(datas.get(position).getContent()));
            if(position==getCount()-1){
                vHolder.lineTwo.setVisibility(View.INVISIBLE);
            }else{
                vHolder.lineTwo.setVisibility(View.VISIBLE);
            }
            if(position==0){
                vHolder.lineOne.setVisibility(View.INVISIBLE);
                vHolder.ivHeadPoint.setImageDrawable(getResources().getDrawable(R.drawable.time_list_circle));
            }else{
                vHolder.lineOne.setVisibility(View.VISIBLE);
                vHolder.ivHeadPoint.setImageDrawable(getResources().getDrawable(R.drawable.time_list_circle_one));
            }
            return convertView;
        }
    }
}
