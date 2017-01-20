package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.main.adapter.PoiListAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuPeng on 2016/8/5.
 * 签到地点微调
 */
public class SignInTuningActivity extends UI implements
        OnGetPoiSearchResultListener {
    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    private boolean isFirstLoc = true; // 是否首次定位
    private BDLocation lastLocation;
    private double mCurrentLantitude, mCurrentLongitude;
    private Marker mCurrentMarker;
    int radius = 500;
    private PoiSearch mPoiSearch = null;
    private ListView lvLocNear;
    private List<PoiInfo> nearList;
    private PoiListAdapter mPoiListAdapter;
    //UiSettings:百度地图 UI 控制器
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_tuning);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.address_tuning;
        setToolBar(R.id.toolbar, options);
        mContext = this;
        initView();
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapview_location);
        lvLocNear = (ListView) findViewById(R.id.lv_location_nearby);
        nearList = new ArrayList<>();
        mPoiListAdapter = new PoiListAdapter(mContext, nearList);
        lvLocNear.setAdapter(mPoiListAdapter);
        lvLocNear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPoiListAdapter.notifyDataSetChanged();
                PoiInfo ad = (PoiInfo) mPoiListAdapter.getItem(i);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ad.location);
                mBaiduMap.animateMapStatus(u);
                mCurrentMarker.setPosition(ad.location);

            }
        });
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        //设置地图不可滑动
        //mUiSettings.setScrollGesturesEnabled(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(u);
                mCurrentMarker.setPosition(latLng);
                findPoi(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
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
            Log.d("SignInTuningActivity", "On location change received:" + location);
            Log.d("SignInTuningActivity", "addr:" + location.getAddrStr());

            if (lastLocation != null) {
                if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
                    Log.d("SignInTuningActivity", "same location, skip refresh");
                    // mMapView.refresh();
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
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    findPoi(marker.getPosition());
                }

                @Override
                public void onMarkerDragStart(Marker marker) {

                }
            });

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
            mBaiduMap.animateMapStatus(u);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchNeayBy();
                }
            }).start();
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    private void findPoi(LatLng latLng){
        LatLng p1ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        double distance = DistanceUtil.getDistance(p1ll, latLng);
        LogUtil.e("SignInTuningActivity", "距离为:==" + distance);
        if (distance >= 2000) {
            Toast.makeText(mContext, "距离初始定位大于一公里",
                    Toast.LENGTH_LONG).show();
            mCurrentMarker.setPosition(p1ll);
        }else{
            reverseGeoCode(latLng);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
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

    private void searchNeayBy() {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword("写字楼");
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.location(new LatLng(mCurrentLantitude, mCurrentLongitude));
        if (radius != 0) {
            option.radius(radius);
        } else {
            option.radius(1000);
        }

        option.pageCapacity(20);
        mPoiSearch.searchNearby(option);

    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult != null) {
            if (poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 0) {
                LogUtil.e("SingInTuningActivity", poiResult.getAllPoi().size() + "个位置");
                for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                    LogUtil.e("SingInTuningActivity", poiResult.getAllPoi().get(i).name);
                }
                nearList.addAll(poiResult.getAllPoi());
                mPoiListAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

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
                Toast.makeText(mContext,
                        "位置：" + result.getAddress(), Toast.LENGTH_LONG)
                        .show();
                nearList.clear();
                nearList.addAll(result.getPoiList());
                mPoiListAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", mCurrentMarker.getPosition().latitude);
        intent.putExtra("address",mCurrentMarker.getPosition().longitude);
        setResult(1001, intent);
        super.onBackPressed();
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
