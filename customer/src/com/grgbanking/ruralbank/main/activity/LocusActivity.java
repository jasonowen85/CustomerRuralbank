package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ServerApi;
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
 * Created by LiuPeng on 2016/8/8.
 * 轨迹
 */
public class LocusActivity extends UI {
    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    private boolean isFirstLoc = true; // 是否首次定位
    private BDLocation lastLocation;
    private double mCurrentLantitude, mCurrentLongitude;
    private Marker mCurrentMarker;
    private String jobOrderId;
    private GeoCoder geoCoder;
    private LatLng bankLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locus);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.locus;
        setToolBar(R.id.toolbar, options);
        mContext = this;
        getParams();
        LogUtil.e("LocusActivity","jobOrderId == "+jobOrderId);
        initView();
    }

    private void getParams() {
        jobOrderId = this.getIntent().getStringExtra("jobOrderId");
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.locus_mapview);

        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        //mLocationClient.start();

        ServerApi.getSignDetail(jobOrderId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String ret_code;
                try {
                    ret_code = response.getString("ret_code");
                    LogUtil.e("LocusActivity", "ret_code==" + ret_code);
                    if (ret_code.equals("0")) {
                        JSONObject jsonObject = response.optJSONObject("lists");
                        LogUtil.e("LocusActivity", "lists==" + jsonObject.toString());
                        String address = jsonObject.optString("bankAddress");
                        reverseGeoCode(address);
                        String str = jsonObject.getString("coordinates");
                        String[] strings = str.split(";");
                        List<LatLng> allPonit = new ArrayList<>();
                        for (int i = 0; i < strings.length; i++) {
                            if(!strings[i].equals("")){
                                String[] strs = strings[i].split(",");
                                LatLng latLng = new LatLng(Double.valueOf(strs[0]),Double.valueOf(strs[1]));
                                allPonit.add(latLng);
                            }
                        }
                        LogUtil.e("LocusActivity",str);
                        canvasLocus(allPonit);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                LogUtil.e("LocusActivity", "sign in fail:" + throwable.getMessage());

            }
        });
    }

    /**
     * 反地理编码得到地址信息
     */
    private void reverseGeoCode(String address) {
        // 创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            }
            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(mContext, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                }else{
                    bankLatLng= result.getLocation();
                    LogUtil.e("qzc","---latitude="+bankLatLng.latitude+"---longitude="+bankLatLng.longitude);
                    // 图标
                    OverlayOptions overlayOptions = new MarkerOptions().position(bankLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_customer)).zIndex(5);
                    mBaiduMap.addOverlay(overlayOptions);
//                    MapStatus mapStatus = new MapStatus.Builder().target(bankLatLng).zoom(12).build();
//                    MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
//                    mBaiduMap.setMapStatus(u);
                }

            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.geocode(new GeoCodeOption().city("").address(address));

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


    private void canvasLocus(List<LatLng> allPonit) {
        if(allPonit.size()==0){
            return;
        }
        if(allPonit.size()>1){
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(this.getResources().getColor(R.color.red)).points(allPonit);
            //添加在地图中
            mBaiduMap.addOverlay(ooPolyline);

            OverlayOptions ooA = new MarkerOptions().position(allPonit.get(allPonit.size()-1)).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.map_supplier))
                    .zIndex(4).draggable(false);
            mBaiduMap.addOverlay(ooA);
            OverlayOptions ooA1 = new MarkerOptions().position(allPonit.get(0)).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.map_supplier_ok))
                    .zIndex(4).draggable(false);
            mBaiduMap.addOverlay(ooA1);
            showAllPoint(getBoundPonits(allPonit));
        }else{
            OverlayOptions ooA = new MarkerOptions().position(allPonit.get(0)).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.map_supplier))
                    .zIndex(4).draggable(false);
            mBaiduMap.addOverlay(ooA);
            MapStatus mapStatus = new MapStatus.Builder().target(allPonit.get(0)).zoom(12).build();
            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mBaiduMap.setMapStatus(u);
        }

    }

    /**
     * 获取所有坐标点的 边界点 用于显示地图全部显示
     */
    public static List<LatLng> getBoundPonits(List<LatLng> contentPonit) {
        if (contentPonit == null || contentPonit.size() == 0) {
            return null;
        }
        List<LatLng> lists = new ArrayList<>();
        //边界点 无所谓命名
        LatLng left = contentPonit.get(0);
        LatLng top = left;
        LatLng right = left;
        LatLng bottom = left;

        for (int i = 0; i < contentPonit.size(); i++) {
            if (contentPonit.get(i).longitude < left.longitude) {
                left = contentPonit.get(i);
            }

            if (contentPonit.get(i).longitude > right.longitude) {
                right = contentPonit.get(i);
            }

            if (contentPonit.get(i).latitude < bottom.latitude) {
                bottom = contentPonit.get(i);
            }
            if (contentPonit.get(i).latitude > top.latitude) {
                top = contentPonit.get(i);
            }
        }

        lists.add(left);
        lists.add(right);
        lists.add(top);
        lists.add(bottom);

        return lists;
    }

    /**
     * 显示所有的点的最适合区域
     *
     * @param boundPonits
     */
    public void showAllPoint(List<LatLng> boundPonits) {
        if (boundPonits == null || boundPonits.size() < 4) {
            return;
        }

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(boundPonits.get(0).latitude, boundPonits.get(0).longitude))
                .include(new LatLng(boundPonits.get(1).latitude, boundPonits.get(1).longitude))
                .include(new LatLng(boundPonits.get(2).latitude, boundPonits.get(2).longitude))
                .include(new LatLng(boundPonits.get(3).latitude, boundPonits.get(3).longitude))
                .build();

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
        mBaiduMap.setMapStatus(u);

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
}
