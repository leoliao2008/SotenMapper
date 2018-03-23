package com.skycaster.sotenmapper.module;

import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.IOException;

/**
 * Created by 廖华凯 on 2018/3/19.
 * 这是控制硕腾平板上的GPS模块的模块类，打开模块电源后，直接利用谷歌api获取gps定位信息，不需要也不可以以串口通讯的形式监听该模块的串口，否则会跟系统gps机制相冲突。
 */

public class GpsModule {
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private GpsStatus.NmeaListener mNmeaListener;

    public GpsModule(LocationManager locationManager) {
        mLocationManager=locationManager;
    }


    /**
     * 打开gps电源，通过回调获取gps定位信息
     * @param locationListener gps位置更新回调，可以获得封装好的gps类
     * @param nmeaListener gps位置更新回调，可以获得gps字符串裸数据
     * @throws IOException
     * @throws SecurityException
     */
    public void powerOn(LocationListener locationListener, GpsStatus.NmeaListener nmeaListener) throws IOException,SecurityException {
        powerOff();//先清掉之前的监听回调（万一有的话）
        mLocationListener=locationListener;
        mNmeaListener=nmeaListener;
        //向系统申请获取gps数据时，gps模块的电源就被打开
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0.01f,
                mLocationListener
        );
        mLocationManager.addNmeaListener(mNmeaListener);
    }

    /**
     * 关闭gps电源
     */
    public void powerOff(){
        //向系统取消gps数据申请时，gps模块的电源就被关闭
        if(mLocationListener!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }
        if(mNmeaListener!=null){
            mLocationManager.removeNmeaListener(mNmeaListener);
        }
    }


}
