package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.skycaster.gps_decipher_lib.GPGGA.GPGGABean;
import com.skycaster.gps_decipher_lib.GPGGA.TbGNGGABean;
import com.skycaster.gps_decipher_lib.GPGSA.GPGSABean;
import com.skycaster.gps_decipher_lib.GPGSA.GPGSAType;
import com.skycaster.gps_decipher_lib.GPGSV.GPGSVBean;
import com.skycaster.gps_decipher_lib.GPSDataExtractor;
import com.skycaster.gps_decipher_lib.GPSDataExtractorCallBack;
import com.skycaster.sotenmapper.activity.SatelliteActivity;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.module.GpsModule;

import java.io.IOException;

/**
 * Created by 廖华凯 on 2018/3/29.
 */

public class SatellitePresenter extends BasePresenter {
    private SatelliteActivity mActivity;
    private GpsModule mGpsModule;
    private String mFixStatus;
    private GPSDataExtractorCallBack mGpsDataExtractorCallBack = new GPSDataExtractorCallBack() {
        @Override
        public void onGetTBGNGGABean(final TbGNGGABean bean) {
            super.onGetTBGNGGABean(bean);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Location location = bean.getLocation();
                    mActivity.getTvLat().setText("北纬："+location.getLatitude());
                    mActivity.getTvLng().setText("东经："+location.getLongitude());
                    mActivity.getTvAlt().setText("海拔："+location.getAltitude());
                }
            });
        }

        @Override
        public void onGetGPGGABean(GPGGABean bean) {
            super.onGetGPGGABean(bean);
        }

        @Override
        public void onGetGPGSABean(GPGSABean bean) {
            super.onGetGPGSABean(bean);
            GPGSAType type = bean.getType();
            if(type==GPGSAType.UNFIX){
                mFixStatus ="UNFIX";
            }else if(type==GPGSAType.FIX3D){
                mFixStatus ="FIX3D";
            }else {
                mFixStatus ="FIX2D";
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.getTvFixStatus().setText(mFixStatus);
                }
            });
        }

        @Override
        public void onGetGPGSVBean(final GPGSVBean bean) {
            super.onGetGPGSVBean(bean);
            mActivity.getSatelliteView().updateSatellites(bean);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.getTvSatCount().setText("卫星数："+bean.getSatCount());
                }
            });
        }
    };




    //*****************************************函数区**************************************//

    public SatellitePresenter(SatelliteActivity activity) {
        mActivity = activity;
        mGpsModule=new GpsModule((LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE));
    }

    @Override
    public void onCreate() {
        try {
            mGpsModule.powerOn(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    byte[] bytes = nmea.getBytes();
                    GPSDataExtractor.decipherData(bytes,bytes.length, mGpsDataExtractorCallBack);
                }
            });
        } catch (IOException e) {
            handleException(e);
        }


    }

    @Override
    public void onStart() {



    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {
        if(mActivity.isFinishing()){
            mGpsModule.powerOff();
        }
    }

    @Override
    public void onDestroy() {

    }
}
