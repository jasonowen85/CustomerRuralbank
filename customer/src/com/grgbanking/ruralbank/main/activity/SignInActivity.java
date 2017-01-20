package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LiuPeng on 2016/8/8.
 * 确认签到
 */
public class SignInActivity extends UI {
    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    private boolean isFirstLoc = true; // 是否首次定位
    private BDLocation lastLocation;
    private double mCurrentLantitude, mCurrentLongitude;
    private Marker mCurrentMarker;
    private TextView addressJustTv, dateTv, timeTv, address1Tv, address2Tv;
    private CircleImageView addressSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.confirm_sign_in;
        setToolBar(R.id.toolbar, options);
        mContext = this;
        initView();
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapview_location);
        addressJustTv = (TextView) findViewById(R.id.address_just);
        dateTv = (TextView) findViewById(R.id.date);
        timeTv = (TextView) findViewById(R.id.time);
        address1Tv = (TextView) findViewById(R.id.address_1);
        address2Tv = (TextView) findViewById(R.id.address_2);
        Date date = new Date();
        SimpleDateFormat timeformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateTv.setText(TimeUtil.getWeekOfDate(date) + " " + timeformatter.format(date));
        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeTv.setText(timeformatter24.format(date));
        addressSignIn = (CircleImageView) findViewById(R.id.address_sign_in);
        addressJustTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationClient != null) {
                    mLocationClient.stop();
                }
                Intent intent = new Intent(mContext, SignInTuningActivity.class);
                startActivityForResult(intent, 1000);
            }

          });
        addressSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerApi.sign("",address2Tv.getText().toString(),new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String ret_code;
                        try {
                            ret_code = response.getString("ret_code");
                            LogUtil.e("SignInActivity", "ret_code==" + ret_code);
                            if (ret_code.equals("0000")) {
                                Toast.makeText(mContext, "签到成功",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        LogUtil.e("SignInActivity", "sign in fail:" + throwable.getMessage());

                    }
                });
            }
        });

        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            address1Tv.setText(location.getStreet() + location.getStreetNumber());
            address2Tv.setText(location.getAddrStr());
            if (lastLocation != null) {
                if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
                    Log.d("SignInActivity", "same location, skip refresh");
                    return;
                }
            }
            lastLocation = location;
            mBaiduMap.clear();
            mCurrentLantitude = lastLocation.getLatitude();
            mCurrentLongitude = lastLocation.getLongitude();
            Log.e(">>>>>>>", mCurrentLantitude + "," + mCurrentLongitude);
            LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();
            converter.coord(llA);
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng convertLatLng = converter.convert();
            OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.supplier_marker))
                    .zIndex(4).draggable(true);
            mCurrentMarker = (Marker) mBaiduMap.addOverlay(ooA);
            mCurrentMarker.setDraggable(true);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
            mBaiduMap.animateMapStatus(u);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 60;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == 1001) {
            //获取从地图微调传来的坐标值
            LatLng latLng = new LatLng(data.getDoubleExtra("result", 0), data.getDoubleExtra("address", 0));
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mCurrentMarker.setPosition(latLng);
            reverseGeoCode(latLng);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 反地理编码得到地址信息
     */
    private void reverseGeoCode(LatLng latLng) {
        // 创建地理编码检索实例
        GeoCoder geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(mContext, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                }
                if (result.getPoiList() != null && result.getPoiList().size() > 0) {
                    address1Tv.setText(result.getPoiList().get(0).name);
                    address2Tv.setText(result.getPoiList().get(0).address);
                }
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        // 释放地理编码检索实例
        //geoCoder.destroy();
    }
}
