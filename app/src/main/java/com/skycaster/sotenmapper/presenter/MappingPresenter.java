package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.inertial_navi_lib.GPGGA.GPGGABean;
import com.skycaster.inertial_navi_lib.GPGGA.TbGNGGABean;
import com.skycaster.inertial_navi_lib.GPGSA.GPGSABean;
import com.skycaster.inertial_navi_lib.GPGSV.GPGSVBean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;
import com.skycaster.inertial_navi_lib.NaviDataExtractorCallBack;
import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sk9042_lib.ack.RequestCallBack;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.MappingActivity;
import com.skycaster.sotenmapper.base.BaseApplication;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.callbacks.AlertDialogCallBack;
import com.skycaster.sotenmapper.impl.Static;
import com.skycaster.sotenmapper.module.BaiduMapModule;
import com.skycaster.sotenmapper.module.CDRadioModule;
import com.skycaster.sotenmapper.module.GpsModule;
import com.skycaster.sotenmapper.utils.AlertDialogUtils;
import com.skycaster.sotenmapper.utils.ToastUtil;

import java.io.IOException;

/**
 * Created by 廖华凯 on 2018/3/26.
 */

public class MappingPresenter extends BasePresenter {
    private MappingActivity mActivity;
    private MapView mMapView;
    private CDRadioModule mCDRadioModule;
    private GpsModule mGpsModule;
    private BaiduMapModule mMapModule;
    private NaviDataExtractorCallBack mNaviDataExtractorCallBack = new NaviDataExtractorCallBack() {
        @Override
        public void onGetGPGSABean(GPGSABean bean) {
            super.onGetGPGSABean(bean);
        }

        @Override
        public void onGetGPGGABean(GPGGABean bean) {
            super.onGetGPGGABean(bean);
            mTextSwitcher.setText(bean.getRawGpggaString());
            mMapModule.updateMyLocation(new LatLng(bean.getLocation().getLatitude(),bean.getLocation().getLongitude()));
        }

        @Override
        public void onGetTBGNGGABean(TbGNGGABean bean) {
            super.onGetTBGNGGABean(bean);
        }

        @Override
        public void onGetGPGSVBean(GPGSVBean bean) {
            super.onGetGPGSVBean(bean);
        }
    };
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
    private RequestCallBack mRequestCallBack = new RequestCallBack() {
        @Override
        public void setFreq(boolean isSuccess) {
            ToastUtil.showToast(mActivity.getString(R.string.sk9042_freq_setting)+isSuccess);
        }

        @Override
        public void getFreq(boolean isAvailable, String freq) {
            StringBuilder sb=new StringBuilder();
            if(isAvailable){
                sb.append(mActivity.getString(R.string.sk9042_current_freq_is)).append(freq);
            }else {
                sb.append(mActivity.getString(R.string.freq_param_is_not_stored_in_flash));
            }
            ToastUtil.showToast(sb.toString());
        }
    };
    private AckDecipher mAckDecipher;
    private TextSwitcher mTextSwitcher;


    //********************************************函数区******************************************//

    public MappingPresenter(MappingActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        mMapView=mActivity.getMapView();
        mAckDecipher = new AckDecipher(mRequestCallBack);
        mMapModule=new BaiduMapModule(mActivity.getMapView().getMap());
        startCdRadio();
        startGps();
        initActionBar();
        initTxtSwitcher();
    }

    private void initTxtSwitcher() {
        mTextSwitcher = mActivity.getTxtSwitcher();
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(mActivity);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        });
        mTextSwitcher.setText(mActivity.getString(R.string.await_module_initializing));
    }

    private void initActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void startGps() {
        mGpsModule=new GpsModule((LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE));
        try {
            mGpsModule.powerOn(mLocationListener, new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    NaviDataExtractor.decipherData(nmea.getBytes(),nmea.length(), mNaviDataExtractorCallBack);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void startCdRadio() {
        mCDRadioModule=new CDRadioModule((PowerManager) mActivity.getSystemService(Context.POWER_SERVICE));
        mCDRadioModule.powerOn();
        String path= BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_SP_PATH,Static.DEFAULT_CD_RADIO_SP_PATH);
        String bd=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_BD_RATES,Static.DEFAULT_CD_RADIO_SP_BD_RATE);
        try {
            mCDRadioModule.openConnection(path,Integer.valueOf(bd), mAckDecipher);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        if(mActivity.isFinishing()){
            mGpsModule.powerOff();
            try {
                mCDRadioModule.closeConnection();
            } catch (Exception e) {
                handleException(e);
            }finally {
                mCDRadioModule.powerOff();
            }
        }
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    public void setCdRadioFreq() {
        AlertDialogUtils.showSK9042GetFreqWindow(
                mActivity,
                new AlertDialogCallBack(){
                    @Override
                    public void onGetInput(String input) {
                        try {
                            mCDRadioModule.setFreq(input);
                        } catch (IOException e) {
                            handleException(e);
                        }
                    }
                }
        );
    }

    public void getCdRadioFreq() {
        try {
            mCDRadioModule.getFreq();
        } catch (IOException e) {
            handleException(e);
        }
    }
}
