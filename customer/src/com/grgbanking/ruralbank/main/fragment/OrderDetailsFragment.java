package com.grgbanking.ruralbank.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.grgbanking.ruralbank.NimApplication;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.api.ServerApi;
import com.grgbanking.ruralbank.common.bean.workOrder;
import com.grgbanking.ruralbank.common.util.UPlayer;
import com.grgbanking.ruralbank.common.util.sys.ImageUtils;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.activity.ImagePreviewActivity;
import com.grgbanking.ruralbank.main.activity.first_workorder_activity;
import com.grgbanking.ruralbank.main.activity.input_courier_number_activity;
import com.grgbanking.ruralbank.main.activity.input_evaluate_activity;
import com.grgbanking.ruralbank.main.event.EventLatLng;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.greenrobot.event.EventBus;


/**
 *
 */
public class OrderDetailsFragment extends Fragment {

    private Context mContext;
    private View view;
    private String mOrderId;
    private UPlayer player;
    private AnimationDrawable drawable; // 录音播放时的动画背景
    private String voiceUrl;
    private ImageView details_ImgVolume;
    private View tweet_layout_record;
    private ImageView iv_picture1, iv_picture2, iv_picture3, iv_picture4, iv_picture5, iv_picture6, iv_picture7, iv_picture8, iv_picture9;
    private ImageView[] iv_pictures = new ImageView[9];
    private ImageView[] iv_picturecompletes = new ImageView[9];
    private TextView tv_workorder_No, tv_singleState, tv_repair_outlets, tv_supplier, tv_equipment, tv_model, tv_note, tv_fault_condition;
    private ImageView iv_action2, iv_action1;
    private ImageView star1, star2, star3, star4, star5;
    private workOrder mWorkOrder;
    private LinearLayout ll_express, ll_evaluate, ll_complete, ll_contact_address;
    private TextView tv_complete, tv_line, tv_contact_phone, tv_therepair_name, tv_contact_address, tv_express, tv_courierNum, tv_evaluate;
    private int mOrderType = 0;//1 上门维修   2 寄件返修

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_details, container, false);
        mContext = getActivity();
        EventBus.getDefault().register(this);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Glide.with(mContext.getApplicationContext()).pauseRequests();
    }

    private void initView() {
        iv_action2 = (ImageView) view.findViewById(R.id.iv_action2);
        iv_action1 = (ImageView) view.findViewById(R.id.iv_action1);
        star1 = (ImageView) view.findViewById(R.id.star1);
        star2 = (ImageView) view.findViewById(R.id.star2);
        star3 = (ImageView) view.findViewById(R.id.star3);
        star4 = (ImageView) view.findViewById(R.id.star4);
        star5 = (ImageView) view.findViewById(R.id.star5);
        tv_express = (TextView) view.findViewById(R.id.tv_express);
        tv_courierNum = (TextView) view.findViewById(R.id.tv_courierNum);
        ll_express = (LinearLayout) view.findViewById(R.id.ll_express);
        ll_complete = (LinearLayout) view.findViewById(R.id.ll_complete);
        ll_evaluate = (LinearLayout) view.findViewById(R.id.ll_evaluate);
        tv_evaluate = (TextView) view.findViewById(R.id.tv_evaluate);
        tv_complete = (TextView) view.findViewById(R.id.tv_complete);
        //tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
        tv_therepair_name = (TextView) view.findViewById(R.id.tv_therepair_name);//维修人
        tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);//维修人电话
        //ll_workorder_tracking = (LinearLayout) view.findViewById(R.id.ll_workorder_tracking);

        tv_line = (TextView) view.findViewById(R.id.tv_line);
        ll_contact_address = (LinearLayout) view.findViewById(R.id.ll_contact_address);
        tv_contact_address = (TextView) view.findViewById(R.id.tv_contact_address);//地址

        iv_picture1 = (ImageView) view.findViewById(R.id.iv_picture1);
        iv_picture2 = (ImageView) view.findViewById(R.id.iv_picture2);
        iv_picture3 = (ImageView) view.findViewById(R.id.iv_picture3);
        iv_picture4 = (ImageView) view.findViewById(R.id.iv_picture4);
        iv_picture5 = (ImageView) view.findViewById(R.id.iv_picture5);
        iv_picture6 = (ImageView) view.findViewById(R.id.iv_picture6);
        iv_picture7 = (ImageView) view.findViewById(R.id.iv_picture7);
        iv_picture8 = (ImageView) view.findViewById(R.id.iv_picture8);
        iv_picture9 = (ImageView) view.findViewById(R.id.iv_picture9);
        iv_pictures[0] = iv_picture1;
        iv_pictures[1] = iv_picture2;
        iv_pictures[2] = iv_picture3;
        iv_pictures[3] = iv_picture4;
        iv_pictures[4] = iv_picture5;
        iv_pictures[5] = iv_picture6;
        iv_pictures[6] = iv_picture7;
        iv_pictures[7] = iv_picture8;
        iv_pictures[8] = iv_picture9;
        ImageView iv_picturecomplete1 = (ImageView) view.findViewById(R.id.iv_picturecomplete1);
        ImageView iv_picturecomplete2 = (ImageView) view.findViewById(R.id.iv_picturecomplete2);
        ImageView iv_picturecomplete3 = (ImageView) view.findViewById(R.id.iv_picturecomplete3);
        ImageView iv_picturecomplete4 = (ImageView) view.findViewById(R.id.iv_picturecomplete4);
        ImageView iv_picturecomplete5 = (ImageView) view.findViewById(R.id.iv_picturecomplete5);
        ImageView iv_picturecomplete6 = (ImageView) view.findViewById(R.id.iv_picturecomplete6);
        ImageView iv_picturecomplete7 = (ImageView) view.findViewById(R.id.iv_picturecomplete7);
        ImageView iv_picturecomplete8 = (ImageView) view.findViewById(R.id.iv_picturecomplete8);
        ImageView iv_picturecomplete9 = (ImageView) view.findViewById(R.id.iv_picturecomplete9);

        iv_picturecompletes[0] = iv_picturecomplete1;
        iv_picturecompletes[1] = iv_picturecomplete2;
        iv_picturecompletes[2] = iv_picturecomplete3;
        iv_picturecompletes[3] = iv_picturecomplete4;
        iv_picturecompletes[4] = iv_picturecomplete5;
        iv_picturecompletes[5] = iv_picturecomplete6;
        iv_picturecompletes[6] = iv_picturecomplete7;
        iv_picturecompletes[7] = iv_picturecomplete8;
        iv_picturecompletes[8] = iv_picturecomplete9;
        tv_workorder_No = (TextView) view.findViewById(R.id.tv_workorder_no);//工单号
        tv_singleState = (TextView) view.findViewById(R.id.tv_singleState);//维修网点
        tv_repair_outlets = (TextView) view.findViewById(R.id.tv_repair_outlets);//工单进度操作
        tv_supplier = (TextView) view.findViewById(R.id.tv_supplier);//供应商
        tv_equipment = (TextView) view.findViewById(R.id.tv_equipment);//设备
        tv_model = (TextView) view.findViewById(R.id.tv_model);//型号
        tv_note = (TextView) view.findViewById(R.id.et_note);//备注
        tv_fault_condition = (TextView) view.findViewById(R.id.tv_fault_condition);//故障情况
        details_ImgVolume = (ImageView) view.findViewById(R.id.details_img_volume);
        tweet_layout_record = view.findViewById(R.id.tweet_layout_record);
        tweet_layout_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceUrl != null)
                    downloadVoice(voiceUrl);
            }
        });
    }

    private void initData() {
        mOrderId = getActivity().getIntent().getStringExtra("mOrderId");
        LogUtil.e("OrderDetailsFragment", "mOrderId == " + mOrderId);
        getOrderData();
    }

    //接收到eventbus通知更新界面
    public void onEventMainThread(EventLatLng event) {
//        tv_workorder_No.setText(event.mWorkOrder.getJobNum());
//        tv_singleState.setText(event.mWorkOrder.getBankName());
//        tv_supplier.setText(event.mWorkOrder.getSupName());
//        tv_equipment.setText(event.mWorkOrder.getDeviceName());
//        tv_model.setText(event.mWorkOrder.getDmName());
//        tv_note.setText(event.mWorkOrder.getRemark());
//        tv_fault_condition.setText(event.mWorkOrder.getSituation());
//        tv_repair_outlets.setText(event.mWorkOrder.getBankName());
//        showPicture(event.arrs, iv_pictures);
//        voiceUrl = event.voiceUrl;
//        if (!voiceUrl.equals("")){
//            tweet_layout_record.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 语音播放效果
     */
    public void startAnim() {
        drawable = (AnimationDrawable) details_ImgVolume.getBackground();
        details_ImgVolume.setVisibility(View.VISIBLE);
        drawable.start();
    }

    private void showPicture(final String[] urls, ImageView[] pictures) {
        if (urls.length == 0) {
            return;
        }
        for (int i = 0; i < urls.length; i++) {
            pictures[i].setVisibility(View.VISIBLE);
            final String url = String.format(ApiHttpClient.API_URL_IMG, urls[i]);
            final String thumbnailurl = String.format(ApiHttpClient.API_URL_IMG, ImageUtils.getThumbnail(urls[i]));
            if (Util.isOnMainThread()) {
                Glide.with(this)
                        .load(thumbnailurl)
                        .centerCrop()
                        .crossFade()
                        .into(pictures[i]);
            }
            Rect frame = new Rect();
            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            final int[] location = new int[2];
            pictures[i].getLocationOnScreen(location);
            location[1] += statusBarHeight;

            final int width = pictures[i].getWidth();
            final int height = pictures[i].getHeight();

            final int finalI = i;
            pictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bundle extras = new Bundle();
//                    extras.putString(ImagesDetailActivity.INTENT_IMAGE_URL_TAG, url);
//                    Intent intent = new Intent(mContext, ImagesDetailActivity.class);
//                    if (null != extras) {
//                        intent.putExtras(extras);
//                    }
//                    startActivity(intent);
                    ImagePreviewActivity.showImagePrivew(mContext, finalI,
                            urls);
                }
            });
        }

        if (urls.length % 3 > 0) {
            for (int i = urls.length; i < 9; i++) {
                if (i < urls.length + (3 - (urls.length % 3))) {
                    pictures[i].setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(R.drawable.kongbai)
                            .centerCrop()
                            .crossFade()
                            .into(pictures[i]);
                }
            }
        }
    }

    private File getPhotoDir(Context ctx) {
        final String dirName = "myrecords";
        File root = ctx.getExternalFilesDir(null);
        File dir = new File(root, dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        return dir;
    }

    private void downloadVoice(String voiceUrl) {
        File file = new File(getPhotoDir(mContext.getApplicationContext()), mOrderId + ".amr");
        if (file.exists()) {
            playSound(file);
        } else {
            ServerApi.download(voiceUrl, new FileAsyncHttpResponseHandler(file) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                    Toast.makeText(mContext, "下载文件失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, File file) {
                    playSound(file);
                }
            });
        }
    }

    private void playSound(File file) {
        if (file != null) {
            if(null == player) {
                player = new UPlayer(file.getPath(),
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 0) {
                                    startAnim();
                                } else if (msg.what == 1) {
                                    if (drawable != null) {
                                        drawable.stop();
                                        details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                                        details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                                    }
                                } else {
                                    if (drawable != null) {
                                        drawable.stop();
                                        details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                                        details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                                    }
                                }
                            }
                        }
                );
                player.start();
            } else if(player.getMediaPlayer().isPlaying()){
                player.stop();
                if (drawable != null) {
                    drawable.stop();
                    details_ImgVolume.setBackgroundResource(R.drawable.icon_record);
                    details_ImgVolume.setBackgroundResource(R.drawable.audio_animations);
                }
//                player = null;
            } else {
                player.start(file.getPath());
                startAnim();
            }
        }
    }



    private void setButtons( String schedule) {
        if ( schedule.equals("3") || schedule.equals("4")) { //   --- 撤单
            iv_action1.setVisibility(View.VISIBLE);
            iv_action1.setImageResource(R.drawable.button1);
            iv_action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(mOrderId);
                    iv_action1.setClickable(false);
                }
            });
        } else if (schedule.equals("11") || schedule.equals("1") ||schedule.equals("12") || schedule.equals("2")) {//撤单、录入快递单号
            iv_action1.setVisibility(View.VISIBLE);
            iv_action1.setImageResource(R.drawable.button1);
            if(mOrderType==2){
                if (mWorkOrder.getExpress() == null || mWorkOrder.getCourierNum() == null || mWorkOrder.getExpress().equals("") || mWorkOrder.getCourierNum().equals("")) {
                    iv_action2.setVisibility(View.VISIBLE);
                    iv_action2.setImageResource(R.drawable.button9);
                }
            }
            iv_action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(mOrderId);
                    iv_action1.setClickable(false);
                }
            });
            iv_action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, input_courier_number_activity.class);
                    it.putExtra("jobOrderId", mOrderId);
                    startActivity(it);
                    iv_action2.setClickable(false);
                    getActivity().finish();

                }
            });
        }else if (schedule.equals("16")) { //   ---   确认收货
            iv_action2.setVisibility(View.VISIBLE);
            iv_action2.setImageResource(R.drawable.button14);

            iv_action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comfirmOrder(mOrderId);
                    iv_action2.setClickable(false);
                }
            });
        } else if (schedule.equals("7")) { //---  确认完成
            iv_action1.setVisibility(View.VISIBLE);
            iv_action1.setImageResource(R.drawable.button12);
            iv_action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comfirmOrder(mOrderId);
                    iv_action1.setClickable(false);
                }
            });
        } else if (schedule.equals("5") ) { //--- 撤单
            iv_action1.setVisibility(View.VISIBLE);
            iv_action1.setImageResource(R.drawable.button1);
            iv_action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(mOrderId);
                    iv_action1.setClickable(false);
                }
            });

        }else if (schedule.equals("13")) { //--- 撤单 录入快递号
            iv_action1.setVisibility(View.VISIBLE);
            iv_action1.setImageResource(R.drawable.button1);
            if(mOrderType==2){
                if (mWorkOrder.getExpress() == null || mWorkOrder.getCourierNum() == null || mWorkOrder.getExpress().equals("") || mWorkOrder.getCourierNum().equals("")) {
                    iv_action2.setVisibility(View.VISIBLE);
                    iv_action2.setImageResource(R.drawable.button9);
                }
            }
            iv_action1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closedOrder(mOrderId);
                    iv_action1.setClickable(false);
                }
            });
            iv_action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, input_courier_number_activity.class);
                    it.putExtra("jobOrderId", mOrderId);
                    startActivity(it);
                    iv_action2.setClickable(false);
                    getActivity().finish();
                }
            });
        }else if(schedule.equals("8")||schedule.equals("17")){
            iv_action2.setVisibility(View.VISIBLE);
            iv_action2.setImageResource(R.drawable.button15);
            iv_action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳到评价页面
                    Intent intent = new Intent(mContext, input_evaluate_activity.class);
                    intent.putExtra("jobOrderId", mOrderId);
                    startActivity(intent);
                    iv_action2.setClickable(false);
                    getActivity().finish();
                }
            });
        }

    }

    /* 2.2.7.确认收货*/
    protected void comfirmOrder(String jobOrder_id) {
        ServerApi.comfirmOrder(jobOrder_id, Preferences.getUserid(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "接单或者确认成功！", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mContext, first_workorder_activity.class);
                    i.putExtra("state", "004");
                    startActivity(i);
                    getActivity().finish();
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

    /*撤单*/
    protected void cancelOrder(String orderid) {
        ServerApi.cancelOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "撤单成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                } else if(ret_code.equals("100")){
                    String ret_msg = response.optString("ret_msg");
                    Toast.makeText(mContext, ret_msg, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
                }else {
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

    /*关闭工单*/
    protected void closedOrder(String orderid) {
        ServerApi.closeOrder(orderid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");
                if (ret_code.equals("0")) {
                    Toast.makeText(mContext, "关闭成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, first_workorder_activity.class);
                    intent.putExtra("state", "001");
                    startActivity(intent);
                    getActivity().finish();
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

    private void getOrderData() {
        ServerApi.getJobOrderDetails(mOrderId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code = response.optString("ret_code");

                if (ret_code.equals("0")) {
                    String json = response.toString();
                    LogUtil.e("SupplierListActivity", "ret_code==" + json);

                    JSONObject jsonOb = response.optJSONObject("lists");
                    JSONObject jsonObj = jsonOb.optJSONObject("lists");
                    JSONObject jsonObj2 = jsonOb.optJSONObject("jobOrderDetail");
                    try {
                        mWorkOrder = new workOrder();
                        mWorkOrder.setJobNum(jsonObj.getString("jobNum"));//工单号
                        mWorkOrder.setBankName(jsonObj.getString("bankName"));
                        if (jsonObj2.has("courierNum") && !StringUtil.isEmpty(jsonObj2.getString("courierNum"))) {
                            tv_express.setText(jsonObj2.getString("express"));//快递公司
                            tv_courierNum.setText(jsonObj2.getString("courierNum"));//快递单号
                            mWorkOrder.setExpress(jsonObj2.getString("express"));
                            mWorkOrder.setCourierNum(jsonObj2.getString("courierNum"));
                            ll_express.setVisibility(View.VISIBLE);
                        }
                        if (jsonObj2.has("evaluate")) {
                            tv_evaluate.setText(jsonObj2.getString("evaluate"));//工单号
                            switch (jsonObj2.getInt("starLevel")) {
                                case 1:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star1);
                                    star3.setImageResource(R.drawable.star1);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 2:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star1);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 3:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star1);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 4:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star2);
                                    star5.setImageResource(R.drawable.star1);
                                    break;
                                case 5:
                                    star1.setImageResource(R.drawable.star2);
                                    star2.setImageResource(R.drawable.star2);
                                    star3.setImageResource(R.drawable.star2);
                                    star4.setImageResource(R.drawable.star2);
                                    star5.setImageResource(R.drawable.star2);
                                    break;
                            }

                            ll_evaluate.setVisibility(View.VISIBLE);
                        }

                        String picUrls = jsonObj.getString("imgSerialNum");
                        mWorkOrder.setSupName(jsonObj.getString("supName"));
                        mWorkOrder.setDeviceName(jsonObj.getString("deviceName"));
                        mWorkOrder.setDmName(jsonObj.getString("dmName"));
                        mWorkOrder.setRemark(jsonObj.getString("remark"));
                        mWorkOrder.setSituation("故障情况：  " + jsonObj.getString("situation"));
                        tv_workorder_No.setText(mWorkOrder.getJobNum());
                        tv_singleState.setText(mWorkOrder.getBankName());
                        tv_supplier.setText(mWorkOrder.getSupName());
                        tv_equipment.setText(mWorkOrder.getDeviceName());
                        tv_model.setText(mWorkOrder.getDmName());
                        tv_note.setText(mWorkOrder.getRemark());
                        tv_fault_condition.setText(mWorkOrder.getSituation());
                        tv_repair_outlets.setText(mWorkOrder.getBankName());
                        mOrderType = jsonObj2.getInt("type");
                        if (mOrderType == 1) {
                            ll_contact_address.setVisibility(View.GONE);
                            tv_line.setVisibility(View.GONE);
                        }
                        if (jsonObj2.has("comAddress")) {
                            tv_contact_address.setText(jsonObj2.getString("comAddress"));
                        }
                        if (jsonObj.has("execution")) {
                            ll_complete.setVisibility(View.VISIBLE);
                            tv_complete.setText(jsonObj.getString("execution"));
                        }
                        if (jsonObj.has("imageStr")) {

                            String picUrl2s = jsonObj.getString("imageStr");
                            String[] arrs = picUrl2s.split(",");
                            showPicture(arrs, iv_picturecompletes);
                        }
                        if (jsonObj2.has("voice")) {
                            voiceUrl = jsonObj2.getString("voice");
                            if(!voiceUrl.equals("")){
                                tweet_layout_record.setVisibility(View.VISIBLE);
                            }
                        }

                        String[] arrs = picUrls.split(",");
                        showPicture(arrs, iv_pictures);
                        //EventBus.getDefault().post(new EventLatLng(mWorkOrder,arrs,voiceUrl));
                        tv_therepair_name.setText(jsonObj.getString("userName"));
                        String contactPhone = jsonObj.optString("phone");
                        if(!TextUtils.isEmpty(contactPhone) && contactPhone.contains(NimApplication.sendApkName) ){
                            tv_contact_phone.setText(contactPhone.trim().substring(NimApplication.sendApkName.length()));
                        } else {
                            tv_contact_phone.setText(jsonObj.getString("phone"));
                        }

                        String schedule = jsonObj.getString("schedule");
                        String state = jsonObj.getString("state");
                        setButtons( schedule);
                        //getTrackingData();
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

}
