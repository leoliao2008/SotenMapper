package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sk9042_lib.ack.RequestCallBack;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.CombineTestActivity;
import com.skycaster.sotenmapper.base.BaseApplication;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.callbacks.AlertDialogCallBack;
import com.skycaster.sotenmapper.impl.Static;
import com.skycaster.sotenmapper.module.CDRadioModule;
import com.skycaster.sotenmapper.module.GpsModule;
import com.skycaster.sotenmapper.utils.AlertDialogUtils;
import com.skycaster.sotenmapper.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/19.
 */

public class CombineTestActivityPresenter extends BasePresenter {
    private CombineTestActivity mActivity;
    private ListView mConsole;
    private ArrayList<String> mMessages=new ArrayList<>();
    private ArrayAdapter<String> mConsoleAdapter;
    private CDRadioModule mCdRadioModule;
    private GpsModule mGpsModule;
    private RequestCallBack mRequestCallBack=new RequestCallBack() {
        @Override
        public void testConnection(boolean isConnected) {
            super.testConnection(isConnected);
            updateConsoleFromThread(isConnected?"模块连接正常":"模块连接失败");

        }

        @Override
        public void reset(boolean isSuccess) {
            super.reset(isSuccess);
            updateConsoleFromThread(isSuccess?"算法重设成功":"算法重设失败");
        }

        @Override
        public void getSysTime(String time) {
            super.getSysTime(time);
            updateConsoleFromThread(time);
        }

        @Override
        public void setBaudRate(boolean isSuccess) {
            super.setBaudRate(isSuccess);
            updateConsoleFromThread(isSuccess?"波特率设置成功":"波特率设置失败");
        }

        @Override
        public void setFreq(boolean isSuccess) {
            super.setFreq(isSuccess);
            updateConsoleFromThread(isSuccess?"频点设置成功":"频点设置失败");
        }

        @Override
        public void getFreq(boolean isAvailable, String freq) {
            super.getFreq(isAvailable, freq);
            updateConsoleFromThread(isAvailable?"当前频点是"+freq:"FLASH没有写入频点信息！");
        }

        @Override
        public void setReceiveMode(boolean isSuccess) {
            super.setReceiveMode(isSuccess);
            updateConsoleFromThread(isSuccess?"设置接收模式成功":"设置接收模式失败");
        }

        @Override
        public void getReceiveMode(String mode) {
            super.getReceiveMode(mode);
            updateConsoleFromThread("当前接收模式为模式"+mode);
        }

        @Override
        public void setRunningMode(boolean isSuccess) {
            super.setRunningMode(isSuccess);
            updateConsoleFromThread(isSuccess?"设置运行模式成功":"设置运行模式失败");
        }

        @Override
        public void getRunningMode(String mode) {
            super.getRunningMode(mode);
            updateConsoleFromThread("当前运行模式为模式"+mode);
        }

        @Override
        public void toggleCKFO(boolean isSuccess) {
            super.toggleCKFO(isSuccess);
            updateConsoleFromThread(isSuccess?"切换数据输出校验功能成功":"切换数据输出校验功能失败！");
        }

        @Override
        public void checkIsOpenCKFO(String isOpen) {
            super.checkIsOpenCKFO(isOpen);
            updateConsoleFromThread("数据输出校验功能当前状态："+isOpen);
        }

        @Override
        public void toggle1PPS(boolean isSuccess) {
            super.toggle1PPS(isSuccess);
            updateConsoleFromThread(isSuccess?"1PPS功能设置成功。":"1PPS功能设置失败！");
        }

        @Override
        public void checkIsOpen1PPS(String isOpen) {
            super.checkIsOpen1PPS(isOpen);
            updateConsoleFromThread("当前1PPS状态："+isOpen);
        }

        @Override
        public void getSysVersion(String version) {
            super.getSysVersion(version);
            updateConsoleFromThread("当前系统版本号："+version);
        }

        @Override
        public void setChipId(boolean isSuccess) {
            super.setChipId(isSuccess);
            updateConsoleFromThread(isSuccess?"设置芯片ID成功。":"设置芯片ID失败。");
        }

        @Override
        public void getChipId(String id) {
            super.getChipId(id);
            updateConsoleFromThread("当前芯片ID:"+id);
        }

        @Override
        public void getSNR(String snr) {
            super.getSNR(snr);
            updateConsoleFromThread("当前音噪率："+snr);
        }

        @Override
        public void getSysState(String state) {
            super.getSysState(state);
            updateConsoleFromThread("当前系统状态："+state);
        }

        @Override
        public void getSFO(String sfo) {
            super.getSFO(sfo);
            updateConsoleFromThread("当前时偏："+sfo);
        }

        @Override
        public void getCFO(String cfo) {
            super.getCFO(cfo);
            updateConsoleFromThread("当前频偏："+cfo);
        }

        @Override
        public void getTunerState(String isSet, String hasData) {
            super.getTunerState(isSet, hasData);
            updateConsoleFromThread((isSet.equals("1")?"Tuner设置成功":"Tuner设置失败")+" "+(hasData.equals("1")?"有数据输入。":"没数据输入。"));
        }

        @Override
        public void getLDPC(String passCnt, String failCnt) {
            super.getLDPC(passCnt, failCnt);
            updateConsoleFromThread("译码统计：成功次数"+passCnt+"失败次数"+failCnt);
        }
    };
    private AckDecipher mAckDecipher=new AckDecipher(mRequestCallBack);
    private LocationManager mLocationManager;
    private LocationListener  mLocationListener = new LocationListener() {
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
    private boolean isCKFOSet;


    /**************************************以下函数区****************************************/

    public CombineTestActivityPresenter(CombineTestActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        initConsole();
        initCdRadioFuncMenu();
        PowerManager powerManager= (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        mCdRadioModule=new CDRadioModule(powerManager);
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mGpsModule=new GpsModule(mLocationManager);
//        initGPS();
//        initCdRadio();
    }

//    private void initGPS() {
//        try {
//            mGpsModule.powerOn(mLocationListener,mNmeaListener);
//        } catch (IOException e) {
//            handleException(e);
//        }
//    }

//    private void initCdRadio() {
//        String path=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_SP_PATH,"/dev/ttyMT1");
//        String bd=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_BD_RATES,"57600");
//        try {
//            connectCdRadio(path,Integer.valueOf(bd),mAckDecipher);
//        } catch (Exception e) {
//            handleException(e);
//        }
//    }

    private void initConsole() {
        mConsole = mActivity.getLstv_feedBackConsole();
        mConsoleAdapter = new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                mMessages
        );
        mConsole.setAdapter(mConsoleAdapter);
    }

    private void updateConsole(String msg){
        mMessages.add(msg);
        mConsoleAdapter.notifyDataSetChanged();
        mConsole.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    private synchronized void updateConsoleFromThread(final String msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConsole(msg);
            }
        });
    }


    private void initCdRadioFuncMenu() {
        ListView listView = mActivity.getLstv_cdRadioFuncMenu();
        final String[] strings = mActivity.getResources().getStringArray(R.array.sk9042_functions);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                strings
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (position){
                        case 0:
                            mCdRadioModule.testConnection();
                            break;
                        case 1:
                            mCdRadioModule.reset();
                            break;
                        case 2:
                            mCdRadioModule.getSysTime();
                            break;
                        case 3:
                            AlertDialogUtils.showSK9042SpConfigWindow(
                                    mActivity,
                                    new AlertDialogCallBack() {
                                        @Override
                                        public void onGetSpParams(String path, String bdRate) {
                                            try {
                                                mCdRadioModule.setBaudRate(path,bdRate);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 4:
                            mCdRadioModule.getBaudRate();
                            break;
                        case 5:
                            AlertDialogUtils.showSK9042SetFreqWindow(
                                    mActivity,
                                    new AlertDialogCallBack(){
                                        @Override
                                        public void onGetInput(String input) {
                                            try {
                                                mCdRadioModule.setFreq(input);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 6:
                            mCdRadioModule.getFreq();
                            break;
                        case 7:
                            AlertDialogUtils.showSK9042SetReceiveModeWindow(
                                    mActivity,
                                    new AlertDialogCallBack(){
                                        @Override
                                        public void onGetInput(String input) {
                                            try {
                                                mCdRadioModule.setReceiveMode(input);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 8:
                            mCdRadioModule.getReceiveMode();
                            break;
                        case 9:
                            AlertDialogUtils.showSK9042SetRunningModeWindow(
                                    mActivity,
                                    new AlertDialogCallBack(){
                                        @Override
                                        public void onGetInput(String input) {
                                            try {
                                                mCdRadioModule.setRunningMode(input);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 10:
                            mCdRadioModule.getRunningMode();
                            break;
                        case 11:
                            isCKFOSet=!isCKFOSet;
                            mCdRadioModule.toggleCKFO(isCKFOSet);
                            updateConsole(mActivity.getString(R.string.CKFO_function)+":"+isCKFOSet);
                            break;
                        case 12:
                            mCdRadioModule.checkIsOpenCKFO();
                            break;
                        case 13:
                            mCdRadioModule.toggle1PPS(true);
                            updateConsole(mActivity.getString(R.string._1pps_function)+true);
                            break;
                        case 14:
                            mCdRadioModule.toggle1PPS(false);
                            updateConsole(mActivity.getString(R.string._1pps_function)+false);
                            break;
                        case 15:
                            mCdRadioModule.checkIsOpen1PPS();
                            break;
                        case 16:
                            mCdRadioModule.beginSysUpgrade();
                            break;
                        case 17:
                            ToastUtil.showToast(mActivity.getString(R.string.function_not_available));
                            break;
                        case 18:
                            mCdRadioModule.getSysVersion();
                            break;
                        case 19:
                            AlertDialogUtils.showSK9042SetChipIdWindow(
                                    mActivity,
                                    new AlertDialogCallBack(){
                                        @Override
                                        public void onGetInput(String input) {
                                            try {
                                                mCdRadioModule.setChipId(input);
                                            } catch (Exception e) {
                                                handleException(e);
                                            }
                                        }
                                    }
                            );
                            break;
                        case 20:
                            mCdRadioModule.getChipId();
                            break;
                        case 21:
                            mCdRadioModule.getSNR();
                            break;
                        case 22:
                            mCdRadioModule.getSysState();
                            break;
                        case 23:
                            mCdRadioModule.getSFO();
                            break;
                        case 24:
                            mCdRadioModule.getCFO();
                            break;
                        case 25:
                            mCdRadioModule.getTunerState();
                            break;
                        case 26:
                            mCdRadioModule.getLDPC();
                            break;
                        case 27:
                            AlertDialogUtils.showSK9042SetLogLevelWindow(mActivity,new AlertDialogCallBack(){
                                @Override
                                public void onGetInput(String input) {
                                    try {
                                        mCdRadioModule.setLogLevel(input);
                                    } catch (Exception e) {
                                        handleException(e);
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    handleException(e);
                }

            }
        });

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
            try {
                mCdRadioModule.closeConnection();
            } catch (InterruptedException e) {
                handleException(e);
            }
        }

    }

    @Override
    public void onDestroy() {

    }

    public void showCdRadioSpConfigWindow() {
        String path=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_SP_PATH,"/dev/ttyMT1");
        String bd=BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_BD_RATES,"57600");
        AlertDialogUtils.showAppSpConfigWindow(
                mActivity,
                path,
                bd,
                new AlertDialogCallBack() {
                    @Override
                    public void onGetSpParams(String path, String bdRate) {
                        SharedPreferences.Editor editor = BaseApplication.getSharedPreferences().edit();
                        editor.putString(Static.CD_RADIO_SP_PATH,path);
                        editor.putString(Static.CD_RADIO_BD_RATES,bdRate);
                        editor.apply();
                        try {
                            connectCdRadio(path,Integer.valueOf(bdRate),mAckDecipher);
                        } catch (Exception e) {
                            handleException(e);
                        }
                    }
                }
        );
    }

    private void connectCdRadio(String path, int bdRate, AckDecipher ackDecipher) throws IOException, SecurityException, InterruptedException {
        showLog("connectCdRadio path="+path+" bdRate="+bdRate);
        mCdRadioModule.openConnection(path,bdRate,ackDecipher);
    }


    public void toggleCdRadioPw(boolean isToOpen){
        if(isToOpen){
            mCdRadioModule.powerOn();
        }else {
            mCdRadioModule.powerOff();
        }
    }

    public void toggleGpsPw(boolean isToOpen){
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

    @Override
    protected void handleException(Exception e) {
        updateConsoleFromThread(e.getMessage());
    }
}
