package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.gps_decipher_lib.GPGGA.GPGGABean;
import com.skycaster.gps_decipher_lib.GPGGA.TbGNGGABean;
import com.skycaster.gps_decipher_lib.GPGSA.GPGSABean;
import com.skycaster.gps_decipher_lib.GPGSV.GPGSVBean;
import com.skycaster.gps_decipher_lib.GPSDataExtractor;
import com.skycaster.gps_decipher_lib.GPSDataExtractorCallBack;
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
import com.skycaster.sotenmapper.module.FileWriterModule;
import com.skycaster.sotenmapper.module.GpsModule;
import com.skycaster.sotenmapper.utils.AlertDialogUtils;
import com.skycaster.sotenmapper.widget.LanternView;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.skycaster.sotenmapper.utils.ToastUtil.showToast;

/**
 * Created by 廖华凯 on 2018/3/26.
 */

public class MappingPresenter extends BasePresenter {
    private MappingActivity mActivity;
    private MapView mMapView;
    private CDRadioModule mCDRadioModule;
    private GpsModule mGpsModule;
    private BaiduMapModule mMapModule;
    private GPSDataExtractorCallBack mGPSDataExtractorCallBack = new GPSDataExtractorCallBack() {
        @Override
        public void onGetGPGSABean(GPGSABean bean) {
            super.onGetGPGSABean(bean);
        }

        @Override
        public void onGetGPGGABean(GPGGABean bean) {
            super.onGetGPGGABean(bean);
        }
        @Override
        public void onGetTBGNGGABean(TbGNGGABean bean) {
            super.onGetTBGNGGABean(bean);
            //展示定位数据
            String rawString = bean.getRawString();
            mTextSwitcher.setText(rawString);
            Location location = bean.getLocation();
            //筛选出正常的坐标，防止一开始在0,0处定位
            if(location.getLatitude()>0&&location.getLongitude()>0){
                //调节灯笼颜色
                mLanternView.updateLantern(bean.getFixQuality());
                //跳到新坐标
                mMapModule.updateMyLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                //在内存中记录最新位置，便于退出时保存到本地
                mLastUpdate= rawString;
            }
            //记录定位数据
            if(mFileWriterModule!=null){
                byte[] bytes = rawString.getBytes();
                try {
                    mFileWriterModule.write(bytes,bytes.length);
                } catch (IOException e) {
                    handleException(e);
                }
            }
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
            showToast(mActivity.getString(R.string.sk9042_freq_setting)+isSuccess);
        }

        @Override
        public void getFreq(boolean isAvailable, String freq) {
            StringBuilder sb=new StringBuilder();
            if(isAvailable){
                sb.append(mActivity.getString(R.string.sk9042_current_freq_is)).append(freq);
            }else {
                sb.append(mActivity.getString(R.string.freq_param_is_not_stored_in_flash));
            }
            showToast(sb.toString());
        }
    };
    private AckDecipher mAckDecipher;
    private TextSwitcher mTextSwitcher;
    private LanternView mLanternView;
    private FrameLayout.LayoutParams mLayoutParams;//txt switch子view的布局参数
    private int mCnt;
    private String[] mTestLines;
    private Handler mHandler=new Handler();
    private FileWriterModule mFileWriterModule;
    private AtomicBoolean isRunningTest=new AtomicBoolean(false);
    private String mLastUpdate;//记录上一次的定位数据，便于下次进来时直接返回上一次位置


    //********************************************函数区******************************************//

    public MappingPresenter(MappingActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        mMapView=mActivity.getMapView();
        mAckDecipher = new AckDecipher(mRequestCallBack);
        mMapModule=new BaiduMapModule(mActivity.getMapView().getMap());
        mLanternView = mActivity.getLanternView();
        startCdRadio();
        startGps();
        initActionBar();
        initTxtSwitcher();
        jumpToLastUpdate();
    }

    /**
     * 跳到上次保存的位置去
     */
    private void jumpToLastUpdate() {
        mLastUpdate=BaseApplication.getSharedPreferences().getString(Static.LAST_UPDATE,null);
        if(!TextUtils.isEmpty(mLastUpdate)){
            TbGNGGABean bean=new TbGNGGABean(mLastUpdate);
            Location location = bean.getLocation();
            mMapModule.updateMyLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    private void initTxtSwitcher() {
        mTextSwitcher = mActivity.getTxtSwitcher();
        mLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(mActivity);
                tv.setLayoutParams(mLayoutParams);
                tv.setTextColor(Color.BLACK);
                tv.setGravity(Gravity.CENTER);
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
                    try {
                        GPSDataExtractor.decipherData(nmea.getBytes(),nmea.length(), mGPSDataExtractorCallBack);
                    }catch (Exception e){
                        handleException(e);
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void stopGps(){
        mGpsModule.powerOff();
    }

    private void startCdRadio() {
        mCDRadioModule=new CDRadioModule((PowerManager) mActivity.getSystemService(Context.POWER_SERVICE));
        mCDRadioModule.powerOn();
//        String path= BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_SP_PATH,Static.DEFAULT_CD_RADIO_SP_PATH);
//        String bd=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_BD_RATES,Static.DEFAULT_CD_RADIO_SP_BD_RATE);
//        try {
//            mCDRadioModule.openConnection(path,Integer.valueOf(bd), mAckDecipher);
//        } catch (Exception e) {
//            handleException(e);
//        }
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
            stopTest();
            stopGps();
            try {
                mCDRadioModule.closeConnection();
            } catch (Exception e) {
                handleException(e);
            }finally {
                mCDRadioModule.powerOff();
            }
            try {
                terminateRecordingGpggaData();
            } catch (IOException e) {
                handleException(e);
            }
            if(!TextUtils.isEmpty(mLastUpdate)){
                saveLastUpdateToDisk(mLastUpdate);
            }

        }
    }

    /**
     * 保存位置信息到本地
     * @param lastUpdate 位置信息
     */
    private void saveLastUpdateToDisk(String lastUpdate){
        BaseApplication
                .getSharedPreferences()
                .edit()
                .putString(Static.LAST_UPDATE,lastUpdate)
                .apply();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    public void setCdRadioFreq() {
        AlertDialogUtils.showSK9042SetFreqWindow(
                mActivity,
                new AlertDialogCallBack(){
                    @Override
                    public void onGetInput(String input) {
                        try {
                            mCDRadioModule.setFreq(input);
                        } catch (Exception e) {
                            handleException(e);
                        }
                    }
                }
        );
    }

    public void getCdRadioFreq() {
        try {
            mCDRadioModule.getFreq();
        } catch (Exception e) {
            handleException(e);
        }
    }


    private Runnable mTestRunnable =new Runnable() {
        @Override
        public void run() {
            if(isRunningTest.get()){
                byte[] bytes = mTestLines[(mCnt++) % mTestLines.length].getBytes();
                GPSDataExtractor.decipherData(bytes,bytes.length,mGPSDataExtractorCallBack);
                mHandler.postDelayed(mTestRunnable,1000);
            }
        }
    };


    public void startTest(){
        stopGps();
        isRunningTest.set(true);
        mTestLines = mActivity.getResources().getStringArray(R.array.gngga_test_lines);
        mCnt = mTestLines.length;
        mHandler.post(mTestRunnable);
    }

    public void stopTest(){
        isRunningTest.set(false);
        mHandler.removeCallbacks(mTestRunnable);
        startGps();
    }

    public void startRecordingGpggaData(){
        AlertDialogUtils.showGenNewFileNameWindow(
                mActivity,
                new AlertDialogCallBack(){
                    @Override
                    public void onGetInput(String input) {
                        try {
                            mFileWriterModule=new FileWriterModule(mActivity,input);
                            showToast("开始记录GPGGA数据。");
                        } catch (IOException e) {
                            handleException(e);
                        }
                    }
                }
        );
    }

    public void terminateRecordingGpggaData() throws IOException {
        if(mFileWriterModule!=null){
            mFileWriterModule.close();
            mFileWriterModule=null;
            showToast("已经终止记录GPGGA数据。");
        }
    }

    public void pauseRecordingGpggaData(){
        if(mFileWriterModule!=null){
            mFileWriterModule.pause();
            showToast("已暂停记录GPGGA数据。");
        }

    }

    public void resumeRecordingCpggaData(){
        if(mFileWriterModule!=null){
            mFileWriterModule.resume();
            showToast("已继续记录GPGGA数据。");
        }

    }
}
