package com.grgbanking.ruralbank.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.common.util.sys.SystemUtil;
import com.grgbanking.ruralbank.common.util.widget.SharePopupWindow;
import com.grgbanking.ruralbank.main.adapter.SimpleFragmentPagerAdapter;
import com.grgbanking.ruralbank.main.fragment.OrderFragmentFactory;
import com.grgbanking.ruralbank.wxapi.WXEntryActivity;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class input_order_details_activity extends UI {
    private String mOrderId;
    private IWXAPI api;
    private Tencent mTencent;
    //自定义的弹出框类
    SharePopupWindow shareDialog;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    private SimpleFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_order_details);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.workorder_details;
        setToolBar(R.id.toolbar, options);
        api = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID, false);
        mTencent = Tencent.createInstance("1105610368", this.getApplicationContext());
        api.registerApp(WXEntryActivity.WX_APP_ID);

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                OrderFragmentFactory.createFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initID();
        getParams();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getParams() {
        mOrderId = this.getIntent().getStringExtra("orderId");
        LogUtil.e("input_order_details_activity","mOrderId == " + mOrderId);
        getIntent().putExtra("mOrderId", mOrderId);
    }

    private void initID() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_btn:
                shareDialog = new SharePopupWindow(input_order_details_activity.this, itemsOnClick);
                //显示窗口
                shareDialog.showAtLocation(input_order_details_activity.this.findViewById(R.id.ll_first_my_fragment), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {

        public void onClick(View v) {
            shareDialog.dismiss();
            switch (v.getId()) {
                case R.id.iv_weixin:
                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = String.format(ApiHttpClient.API_URL_ORDER, mOrderId);
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = "运维服务工单";
                    msg.description = "工单号:" + mOrderId;

                    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo1);
                    msg.setThumbImage(thumb);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = SystemUtil.buildTransaction("webpage");
                    req.message = msg;
                    // req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    api.sendReq(req);
                    break;
                case R.id.iv_qq:
                    final Bundle params = new Bundle();
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, "运维服务工单");
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "工单号:" + mOrderId);
                    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, String.format(ApiHttpClient.API_URL_ORDER, mOrderId));
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://211.149.174.33:80/equipwarranty/api/image/get?dir=appFiles/systemImages/write_jo.png");
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "运维服务");
                    mTencent.shareToQQ(input_order_details_activity.this, params, new BaseUiListener());
                    break;
                default:
                    break;
            }
        }
    };


    private class BaseUiListener implements IUiListener {

        public void onComplete(JSONObject response) {
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(input_order_details_activity.this, e.errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(input_order_details_activity.this, "取消分享", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

//    class ViewHolder {
//        TextView tv_content, tv_express, tv_time,tv_SignAddress;
//        MapView mv_map;
//    }
//
//    class ListAdapt extends BaseAdapter {
//        private Context mContext;
//        private LayoutInflater mLayoutInflater;
//        private BaiduMap mBaiduMap;
//
//        public ListAdapt(Context context) {
//            mContext = context;
//            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            return datas.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder vHolder = null;
//            if (convertView == null) {
//                vHolder = new ViewHolder();
//                convertView = mLayoutInflater.inflate(R.layout.job_order_tracking_listview, null);
//                vHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                vHolder.tv_express = (TextView) convertView.findViewById(R.id.tv_express);
//                vHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
//                vHolder.tv_SignAddress=(TextView)convertView.findViewById(R.id.tv_SignAddress);
//                vHolder.mv_map = (MapView) convertView.findViewById(R.id.mv_map);
//                convertView.setTag(vHolder);
//            } else {
//                vHolder = (ViewHolder) convertView.getTag();
//            }
//            vHolder.tv_time.setText(datas.get(position).getCreateTime());
//            if (datas.get(position).getExpress() != null) {
//                vHolder.tv_express.setVisibility(View.VISIBLE);
//                vHolder.tv_express.setText("快递：" + datas.get(position).getExpress() + " " + datas.get(position).getCourierNum());
//            }else{
//                vHolder.tv_express.setVisibility(View.GONE);
//            }
//            //当进度为已接单，显示一个地图，然后点击进入看接单人的运动轨迹
//            if (datas.get(position).getState().equals("14")){
//                vHolder.mv_map.setVisibility(View.VISIBLE);
//                mBaiduMap = vHolder.mv_map.getMap();
//                if(datas.get(position).getCoordinates()!=null&&!datas.get(position).getCoordinates().equals("")){
//                    String[] strings = datas.get(position).getCoordinates().split(",");
//                    LatLng latLng = new LatLng(Double.valueOf(strings[0]),Double.valueOf(strings[1]));
//                    OverlayOptions ooA = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
//                            .fromResource(R.drawable.map_supplier))
//                            .zIndex(4).draggable(false);
//                    mBaiduMap.addOverlay(ooA);
//                    MapStatus mMapStatus = new MapStatus.Builder()
//                            .target(latLng)
//                            .zoom(16)
//                            .build();
//                    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//                    //改变地图状态
//                    mBaiduMap.setMapStatus(mMapStatusUpdate);
////                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 16.0f);
////                    mBaiduMap.animateMapStatus(u);
//                }
//                mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng latLng) {
//                        Intent intent = new Intent(input_order_details_activity.this,LocusActivity.class);
//                        intent.putExtra("jobOrderId", mOrderId);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public boolean onMapPoiClick(MapPoi mapPoi) {
//                        return false;
//                    }
//                });
//            }else {
//                vHolder.mv_map.setVisibility(View.GONE);
//            }
//            vHolder.mv_map.showScaleControl(false);
//            vHolder.mv_map.showZoomControls(false);
//            if (datas.get(position).getState().equals("15")){
//                vHolder.tv_SignAddress.setVisibility(View.VISIBLE);
//                vHolder.tv_SignAddress.setText(datas.get(position).getSignAddress());
//            }else {
//                vHolder.tv_SignAddress.setVisibility(View.GONE);
//            }
//            vHolder.tv_content.setText(Html.fromHtml(datas.get(position).getContent()));
//            return convertView;
//        }
//    }
}
