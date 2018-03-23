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
import com.skycaster.sotenmapper.module.PowCtrlModule;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/21.
 */

public class GPSTestPresenter extends BasePresenter {
    private GPSTestActivity mActivity;
    private PowCtrlModule mPowCtrlModule;
//    private GpsModule mGpsModule;
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



    //***********************************************func****************************************
    public GPSTestPresenter(GPSTestActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        PowerManager manager= (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        mLocationManager= (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mPowCtrlModule=new PowCtrlModule(manager);
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
        mPowCtrlModule.gpsPower(mLocationManager, isToOpen, mLocationListener, mNmeaListener);
    }

    public void ctrlSk9042Power(boolean isToOpen){
        mPowCtrlModule.cdRadioPower(isToOpen);
    }

//    public void serialPortSetting(){
//        SharedPreferences spf = BaseApplication.getSharedPreferences();
//        String path = spf.getString(Static.GPS_SP_PATH, Static.DEFAULT_GPS_SP_PATH);
//        String rate = spf.getString(Static.GPS_BD_RATE, Static.DEFAULT_GPS_SP_BD_RATE);
//        AlertDialogUtils.showAppSpConfigWindow(
//                mActivity,
//                path,
//                rate,
//                new AlertDialogCallBack(){
//                    @Override
//                    public void onGetSpParams(String path, String bdRate) {
//                        try {
//                            mGpsModule.powerOn(path, Integer.valueOf(bdRate), new GpsModule.PortDataListener() {
//                                @Override
//                                public void onGetPortData(byte[] data, int len) {
//                                    updateConsole(new String(data,0,len));
//                                }
//                            });
//                        } catch (Exception e) {
//                            handleException(e);
//                        }
//                    }
//                }
//        );
//    }



    @Override
    public void onStart() {


    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {
//        if(mActivity.isFinishing()){
//            mGpsModule.powerOff();
//        }

    }

    @Override
    public void onDestroy() {

    }
}
