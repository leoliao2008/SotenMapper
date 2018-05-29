package com.skycaster.sotenmapper.module;

import android.os.PowerManager;
import android.util.Log;

import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sotenmapper.impl.Static;
import com.skycaster.sotenmapper.utils.StreamOptimizer;
import com.soten.libs.utils.LogUtils;
import com.soten.libs.utils.PowerManagerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import project.SerialPort.SerialPort;

import static com.skycaster.sk9042_lib.request.RequestManager.getInstance;

/**
 * Created by 廖华凯 on 2018/3/17.
 * 用来控制SK9042模块类
 */

public class CDRadioModule {
    private SerialPortModule mSerialPortModule;
    private SerialPort mSerialPort;
    private PowerManager mPowerManager;
    private volatile boolean isInterrupted;
    private Thread mRevTread;
    private byte[] mPortData=new byte[512];
    private InputStream mInputStream;
    private AckDecipher mAckDecipher;


    public CDRadioModule(PowerManager manager) {
        mSerialPortModule=new SerialPortModule();
        mPowerManager=manager;

    }

    /**
     * 打开模块电源
     */
    public void powerOn(){
        PowerManagerUtils.open(mPowerManager,0x02);
    }

    /**
     * 关闭模块电源
     */
    public void powerOff(){
        PowerManagerUtils.close(mPowerManager,0x02);
    }


    /**
     * 打开SK9042模块的串口，在子线程中监听串口数据，解析卫星数据
     * @param ackDecipher
     * @throws Exception
     */
    public void openConnection(final AckDecipher ackDecipher) throws Exception{
        closeConnection();
        mSerialPort=mSerialPortModule.openSerialPort(Static.DEFAULT_CD_RADIO_SP_PATH,Static.DEFAULT_CD_RADIO_SP_BD_RATE);
        if(mSerialPort!=null){
//            showLog("sp != null! begin to read sp......");
            mRevTread = new Thread(new Runnable() {
                @Override
                public void run() {
//                    showLog("thread starts...");
                    isInterrupted=false;
                    mInputStream = mSerialPort.getInputStream();
                    int fd = mSerialPort.getParcelFileDescriptor().getFd();
                    StreamOptimizer optimizer = new StreamOptimizer();
                    optimizer.open();
                    optimizer.addFd(fd,1);
//                    showLog("isInterrupted = "+isInterrupted);
                    while (!isInterrupted){
                        try {
                            int available = optimizer.pollInner(300);
//                            showLog("estimated data len ="+available);
                            if(available>0){
                                int len = mInputStream.read(mPortData);
                                if(len>0){
                                    Log.e("Port Data",new String(mPortData,0,len));
                                    try {
                                        ackDecipher.decipherByBytes(mPortData,len);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            showLog("openConnection error:"+e.getMessage());
                            break;
                        }
                    }
                    optimizer.removeFd(fd);
                    optimizer.close();
                    optimizer=null;
//                    showLog("thread stops...");
                }
            });
            mRevTread.start();
        }else {
            showLog("sp == null! Unable to read sp!");
        }

    }


    /**
     * 关闭SK9042的串口，停止监听串口数据。
     * @throws Exception
     */
    public void closeConnection() throws Exception {
//        showLog("closeConnection  -start");
        if(mRevTread!=null&&mRevTread.isAlive()){
            isInterrupted=true;
            mRevTread.join();
        }
        if(mAckDecipher!=null){
            mAckDecipher.stopDecipherByStream();
        }
        if(mSerialPort!=null){
            mSerialPortModule.close(mSerialPort);
            mSerialPort=null;
        }
//        showLog("closeConnection  -complete");

    }

    public void testConnection() throws Exception {
        getInstance().testConnection(mSerialPort.getOutputStream());
    }
    public void reset() throws Exception {
        getInstance().reset(mSerialPort.getOutputStream());
    }
    public void getSysTime() throws Exception {
        getInstance().getSysTime(mSerialPort.getOutputStream());
    }
    public void setBaudRate(String bdRate) throws Exception {
        getInstance().setBaudRate(
                mSerialPort.getOutputStream(),
                bdRate
        );

    }
    public void getBaudRate() throws Exception {
        getInstance().getBaudRate(mSerialPort.getOutputStream());
    }
    public void setFreq(String freq) throws Exception{
        getInstance().setFreq(mSerialPort.getOutputStream(),freq);
    }
    public void getFreq() throws Exception {
        getInstance().getFreq(mSerialPort.getOutputStream());

    }
    public void setReceiveMode(String mode) throws Exception{
        getInstance().setReceiveMode(mSerialPort.getOutputStream(),mode);

    }
    public void getReceiveMode() throws Exception {
        getInstance().getReceiveMode(mSerialPort.getOutputStream());

    }
//    public void setRunningMode(String mode) throws IOException, InputFormatException,NumberFormatException {
//        getInstance().setRunningMode(mSerialPort.getOutputStream(),mode);
//    }
//    public void getRunningMode() throws IOException {
//        getInstance().getRunningMode(mSerialPort.getOutputStream());
//
//    }
    public void toggleCKFO(boolean isToOpen) throws Exception {
        getInstance().toggleCKFO(mSerialPort.getOutputStream(),isToOpen);
    }
    public void checkIsOpenCKFO() throws Exception {
        getInstance().checkIsOpenCKFO(mSerialPort.getOutputStream());

    }
    public void toggle1PPS(boolean isToOpen) throws Exception {
        getInstance().toggle1PPS(mSerialPort.getOutputStream(),isToOpen);

    }
    public void checkIsOpen1PPS() throws Exception {
        getInstance().checkIsOpen1PPS(mSerialPort.getOutputStream());
    }
    public void beginSysUpgrade(File srcFile) throws Exception {
//        getInstance().beginSysUpgrade(mSerialPort.getOutputStream());
        getInstance().startUpgrade(mSerialPort.getOutputStream(),srcFile);
    }
    public void getSysVersion() throws Exception {
        getInstance().getSysVersion(mSerialPort.getOutputStream());

    }
    public void setChipId(String id) throws Exception{
        getInstance().setChipId(mSerialPort.getOutputStream(),id);

    }
    public void getChipId() throws Exception {
        getInstance().getChipId(mSerialPort.getOutputStream());

    }
    public void getSNR() throws Exception {
        getInstance().getSNR(mSerialPort.getOutputStream());

    }
    public void getSysState() throws Exception {
        getInstance().getSysState(mSerialPort.getOutputStream());

    }
    public void getCFO() throws IOException {
        getInstance().getCFO(mSerialPort.getOutputStream());

    }
    public void getSFO() throws Exception {
        getInstance().getSFO(mSerialPort.getOutputStream());

    }
    public void getTunerState() throws Exception {
        getInstance().getTunerState(mSerialPort.getOutputStream());

    }
    public void getLDPC() throws Exception {
        getInstance().getLDPC(mSerialPort.getOutputStream());

    }
    public void setLogLevel(String level) throws Exception{
        getInstance().setLogLevel(mSerialPort.getOutputStream(),level);

    }

    private void showLog(String msg){
        LogUtils.e(getClass().getSimpleName(),msg);
    }

    public void searchFreq() throws IOException {
        getInstance().startMatchFreq(mSerialPort.getOutputStream());
    }

    public void stopSearchFreq() throws Exception {
        getInstance().stopMatchFreq(mSerialPort.getOutputStream());
    }

    public void verifyFreq(String freq) throws Exception {
        getInstance().verifyFreq(mSerialPort.getOutputStream(),freq);
    }
}
