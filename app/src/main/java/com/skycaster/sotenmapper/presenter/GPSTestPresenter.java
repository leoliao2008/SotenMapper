package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.GPSTestActivity;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.module.CDRadioModule;
import com.skycaster.sotenmapper.module.GpsModule;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/21.
 */

public class GPSTestPresenter extends BasePresenter {
    private GPSTestActivity mActivity;
    private ListView mConsole;
    private ArrayList<String> mStrings=new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
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
    };
    private GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            updateConsole(nmea);
        }
    };
    private GpsModule mGpsModule;
    private CDRadioModule mCDRadioModule;



    //***********************************************func****************************************
    public GPSTestPresenter(GPSTestActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        PowerManager manager= (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        mCDRadioModule=new CDRadioModule(manager);
        mLocationManager= (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mGpsModule=new GpsModule(mLocationManager);
//        mGpsModule=new GpsModule();
        initConsole();
        initActionbar();

    }

    private void initActionbar() {
        try {
            mActivity.getSupportActionBar().setTitle(R.string.gps_module_test);
        }catch (Exception e){
            handleException(e);
        }
    }

    private void initConsole() {
        mConsole = mActivity.getListView();
        mAdapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                mStrings
        );
        mConsole.setAdapter(mAdapter);
    }

    private void updateConsole(final String msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStrings.add(msg);
                mAdapter.notifyDataSetChanged();
                mConsole.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
    }

    public void ctrlGpsPower(boolean isToOpen){
        if(isToOpen){
            try {
                mGpsModule.powerOn(mLocationListener,mNmeaListener);
            } catch (IOException e) {
                handleException(e);
            }
        }else {
            mGpsModule.powerOff();
        }

    }

    public void ctrlSk9042Power(boolean isToOpen){
        if(isToOpen){
            mCDRadioModule.powerOn();
        }else {
            mCDRadioModule.powerOff();
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
            mCDRadioModule.powerOff();
            mGpsModule.powerOff();
        }
    }

    @Override
    public void onDestroy() {

    }
}
