package com.skycaster.sotenmapper.module;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by 廖华凯 on 2018/3/28.
 */

public class BaiduMapModule {
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc=true;
    private CoordinateConverter mConverter;

    public BaiduMapModule(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        mConverter = new CoordinateConverter();
    }

    /**
     * 在地图上跳到指定的位置
     * @param latLng 目标坐标
     */
    public void updateMyLocation(LatLng latLng){
        latLng= getDummyCoord(latLng);
        MyLocationData locData = new MyLocationData.Builder()
                .latitude(latLng.latitude)
                .longitude(latLng.latitude).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

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
