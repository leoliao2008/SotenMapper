package com.skycaster.sotenmapper.module;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.skycaster.sotenmapper.R;

/**
 * Created by 廖华凯 on 2018/3/28.
 */

public class BaiduMapModule {
    private BaiduMap mBaiduMap;
    private CoordinateConverter mConverter;
    private float mZoom=18.0f;
    private MarkerOptions mLocationMarker;
    private MyLocationConfiguration mMyLocationConfig;
    private MapStatus.Builder mBuilder;

    public BaiduMapModule(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
        mBaiduMap.setBuildingsEnabled(false);
        mBaiduMap.setMyLocationEnabled(true);
        mMyLocationConfig = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING,
                true,
                BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));
        mBuilder = new MapStatus.Builder();
        mConverter = new CoordinateConverter();
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mZoom = mapStatus.zoom;
            }
        });
        mLocationMarker = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));
    }

    /**
     * 在地图上跳到指定的位置
     * @param latLng 目标坐标
     */
    public void updateMyLocation(LatLng latLng){
        latLng= getDummyCoord(latLng);
        MapStatus.Builder builder=new MapStatus.Builder().target(latLng).zoom(mZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mBaiduMap.clear();
        mLocationMarker.position(latLng);
        mBaiduMap.addOverlay(mLocationMarker);

    }
//    public void updateMyLocation(LatLng latLng){
//        latLng= getDummyCoord(latLng);
//        MyLocationData locData = new MyLocationData.Builder()
//                .latitude(latLng.latitude)
//                .longitude(latLng.latitude)
//                .build();
//        mBaiduMap.setMyLocationData(locData);
//        mBaiduMap.setMyLocationConfiguration(mMyLocationConfig);
//        mBuilder.target(latLng).zoom(mZoom);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));
//    }

    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     * @param latLng 原始GPS坐标
     * @return 百度坐标
     */
    private LatLng getDummyCoord(LatLng latLng){
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(latLng);
        return mConverter.convert();
    }


}
