package com.skycaster.sotenmapper.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sk9042_lib.ack.RequestCallBack;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.CDRadioTestActivity;
import com.skycaster.sotenmapper.base.BaseApplication;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.callbacks.AlertDialogCallBack;
import com.skycaster.sotenmapper.impl.Static;
import com.skycaster.sotenmapper.module.CDRadioModule;
import com.skycaster.sotenmapper.utils.AlertDialogUtils;
import com.skycaster.sotenmapper.utils.StreamOptimizer;
import com.skycaster.sotenmapper.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import project.SerialPort.SerialPort;

/**
 * Created by 廖华凯 on 2018/3/22.
 */

public class CDRadioTestPresenter extends BasePresenter {
    private CDRadioTestActivity mActivity;
    private CDRadioModule mCdRadioModule;
    private boolean isCKFOSet;
    private ArrayList<String> mMessages =new ArrayList<>();
    private ListView mConsole;
    private ArrayAdapter<String> mAdapter;
    private RequestCallBack mRequestCallBack=new RequestCallBack() {
        @Override
        public void testConnection(boolean isConnected) {
            super.testConnection(isConnected);
            updateConsole(isConnected?"模块连接正常":"模块连接失败");
        }

        @Override
        public void reset(boolean isSuccess) {
            super.reset(isSuccess);
            updateConsole(isSuccess?"算法重设成功":"算法重设失败");
        }

        @Override
        public void getSysTime(String time) {
            super.getSysTime(time);
            updateConsole(time);
        }

        @Override
        public void setBaudRate(boolean isSuccess) {
            super.setBaudRate(isSuccess);
            updateConsole(isSuccess?"波特率设置成功":"波特率设置失败");
        }

        @Override
        public void setFreq(boolean isSuccess) {
            super.setFreq(isSuccess);
            updateConsole(isSuccess?"频点设置成功":"频点设置失败");
        }

        @Override
        public void getFreq(boolean isAvailable, String freq) {
            super.getFreq(isAvailable, freq);
            updateConsole(isAvailable?"当前频点是"+freq:"FLASH没有写入频点信息！");
        }

        @Override
        public void setReceiveMode(boolean isSuccess) {
            super.setReceiveMode(isSuccess);
            updateConsole(isSuccess?"设置接收模式成功":"设置接收模式失败");
        }

        @Override
        public void getReceiveMode(String mode) {
            super.getReceiveMode(mode);
            updateConsole("当前接收模式为模式"+mode);
        }

//        @Override
//        public void setRunningMode(boolean isSuccess) {
//            super.setRunningMode(isSuccess);
//            updateConsole(isSuccess?"设置运行模式成功":"设置运行模式失败");
//        }
//
//        @Override
//        public void getRunningMode(String mode) {
//            super.getRunningMode(mode);
//            updateConsole("当前运行模式为模式"+mode);
//        }

        @Override
        public void toggleCKFO(boolean isSuccess) {
            super.toggleCKFO(isSuccess);
            updateConsole(isSuccess?"切换数据输出校验功能成功":"切换数据输出校验功能失败！");
        }

        @Override
        public void checkIsOpenCKFO(String isOpen) {
            super.checkIsOpenCKFO(isOpen);
            updateConsole("数据输出校验功能当前状态："+isOpen);
        }

        @Override
        public void toggle1PPS(boolean isSuccess) {
            super.toggle1PPS(isSuccess);
            updateConsole(isSuccess?"1PPS功能设置成功。":"1PPS功能设置失败！");
        }

        @Override
        public void checkIsOpen1PPS(String isOpen) {
            super.checkIsOpen1PPS(isOpen);
            updateConsole("当前1PPS状态："+isOpen);
        }

        @Override
        public void getSysVersion(String version) {
            super.getSysVersion(version);
            updateConsole("当前系统版本号："+version);
        }

        @Override
        public void setChipId(boolean isSuccess) {
            super.setChipId(isSuccess);
            updateConsole(isSuccess?"设置芯片ID成功。":"设置芯片ID失败。");
        }

        @Override
        public void getChipId(String id) {
            super.getChipId(id);
            updateConsole("当前芯片ID:"+id);
        }

        @Override
        public void getSNR(String snr) {
            super.getSNR(snr);
            updateConsole("当前音噪率："+snr);
        }

        @Override
        public void getSysState(String state) {
            super.getSysState(state);
            updateConsole("当前系统状态："+state);
        }

        @Override
        public void getSFO(String sfo) {
            super.getSFO(sfo);
            updateConsole("当前时偏："+sfo);
        }

        @Override
        public void getCFO(String cfo) {
            super.getCFO(cfo);
            updateConsole("当前频偏："+cfo);
        }

        @Override
        public void getTunerState(String isSet, String hasData) {
            super.getTunerState(isSet, hasData);
            updateConsole((isSet.equals("1")?"Tuner设置成功":"Tuner设置失败")+" "+(hasData.equals("1")?"有数据输入。":"没数据输入。"));
        }

        @Override
        public void getLDPC(String passCnt, String failCnt) {
            super.getLDPC(passCnt, failCnt);
            updateConsole("译码统计：成功次数"+passCnt+"失败次数"+failCnt);
        }

        @Override
        public void setLogLevel(boolean isSuccess) {
            super.setLogLevel(isSuccess);
            updateConsole("Log等级设置："+isSuccess);
        }
    };
    private AckDecipher mAckDecipher=new AckDecipher(mRequestCallBack);
    private Thread mGpsRevThread;
    private volatile boolean isGpsInterrupt;

    public CDRadioTestPresenter(CDRadioTestActivity activity) {
        mActivity = activity;
        mCdRadioModule=new CDRadioModule((PowerManager) mActivity.getSystemService(Context.POWER_SERVICE));
    }

    @Override
    public void onCreate() {
        initTestMenu();
        initConsole();
        try {
            mActivity.getSupportActionBar().setTitle(mActivity.getString(R.string.sk9042_module_test));
        }catch (NullPointerException e){
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
            try {
                mCdRadioModule.closeConnection();
            } catch (InterruptedException e) {
                handleException(e);
            }
            //测试用
            closeGpsThread();
        }

    }

    @Override
    public void onDestroy() {
    }

    public void toggleCdRadioPw(boolean isToOpen){
        if(isToOpen){
            mCdRadioModule.powerOn();
        }else {
            mCdRadioModule.powerOff();
        }

    }

    public void showSpSettingWindow(){
        //3月28日为了测试展示暂时屏蔽这些代码
        String path = BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_SP_PATH, Static.DEFAULT_CD_RADIO_SP_PATH);
        String rate = BaseApplication.getSharedPreferences().getString(Static.CD_RADIO_BD_RATES, Static.DEFAULT_CD_RADIO_SP_BD_RATE);


        //String path = BaseApplication.getSharedPreferences().getString(Static.GPS_SP_PATH, Static.DEFAULT_GPS_SP_PATH);
        //String rate = BaseApplication.getSharedPreferences().getString(Static.GPS_BD_RATE, Static.DEFAULT_GPS_SP_BD_RATE);

        AlertDialogUtils.showAppSpConfigWindow(
                mActivity,
                path,
                rate,
                new AlertDialogCallBack(){
                    @Override
                    public void onGetSpParams(String path, String bdRate) {

                        try {
                            //3月28日为了测试展示暂时屏蔽这些代码
                            SharedPreferences.Editor edit = BaseApplication.getSharedPreferences().edit();
                            edit.putString(Static.CD_RADIO_SP_PATH,path);
                            edit.putString(Static.CD_RADIO_BD_RATES,bdRate);
                            edit.apply();
                            mCdRadioModule.openConnection(path,Integer.valueOf(bdRate),mAckDecipher);

                            //testGpsSp(path,bdRate);//测试用
                        } catch (Exception e) {
                            handleException(e);
                        }

                    }
                }
        );
    }

    private void closeGpsThread(){
        if(mGpsRevThread!=null&&mGpsRevThread.isAlive()){
            isGpsInterrupt=true;
            try {
                mGpsRevThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void testGpsSp(String path, String bdRate) {
        closeGpsThread();
        SharedPreferences.Editor edit = BaseApplication.getSharedPreferences().edit();
        edit.putString(Static.GPS_SP_PATH,path);
        edit.putString(Static.GPS_BD_RATE,bdRate);
        edit.apply();
        try {
            final SerialPort sp=new SerialPort(new File(path),Integer.valueOf(bdRate),0);
            final InputStream inputStream = sp.getInputStream();
            mGpsRevThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] temp=new byte[512];
                    StreamOptimizer optimizer=new StreamOptimizer();
                    optimizer.open();
                    optimizer.addFd(sp.getParcelFileDescriptor().getFd(),1);
                    isGpsInterrupt=false;
                    while (!isGpsInterrupt){
                        int available = optimizer.pollInner(300);
                        showLog("available ="+available);
                        if(available>0){
                            try {
                                int read = inputStream.read(temp);
                                updateConsole(new String(temp));
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                    optimizer.removeFd(sp.getParcelFileDescriptor().getFd());
                    optimizer.close();
                }
            });
            mGpsRevThread.start();
        } catch (IOException e) {
            handleException(e);
        }
    }

    private void initConsole() {
        mConsole = mActivity.getListViewConsole();
        mAdapter = new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                mMessages
        );
        mConsole.setAdapter(mAdapter);
    }

    private void updateConsole(final String msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessages.add(msg);
                mAdapter.notifyDataSetChanged();
                mConsole.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
    }

    private void initTestMenu() {
        ListView listView = mActivity.getListViewTestMenu();
        String[] funcs = mActivity.getResources().getStringArray(R.array.sk9042_functions);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                funcs
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
                            ToastUtil.showToast(mActivity.getString(R.string.function_deleted));
//                            AlertDialogUtils.showSK9042SetRunningModeWindow(
//                                    mActivity,
//                                    new AlertDialogCallBack(){
//                                        @Override
//                                        public void onGetInput(String input) {
//                                            try {
//                                                mCdRadioModule.setRunningMode(input);
//                                            } catch (Exception e) {
//                                                handleException(e);
//                                            }
//                                        }
//                                    }
//                            );
                            break;
                        case 10:
                            ToastUtil.showToast(mActivity.getString(R.string.function_deleted));
//                            mCdRadioModule.getRunningMode();
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
}
