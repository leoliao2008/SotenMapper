package com.skycaster.sotenmapper.module;

import android.os.PowerManager;

import com.skycaster.sk9042_lib.ack.AckDecipher;
import com.skycaster.sk9042_lib.request.InputFormatException;
import com.soten.libs.utils.LogUtils;
import com.soten.libs.utils.PowerManagerUtils;

import java.io.IOException;
import java.io.InputStream;

import project.SerialPort.SerialPort;

import static com.skycaster.sk9042_lib.request.RequestManager.getInstance;

/**
 * Created by 廖华凯 on 2018/3/17.
 * 用来控制CDRadio模块（如：SK9042属于CDRadio模块）的一个类
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


    public void openConnection(String path, int bdRate, final AckDecipher ackDecipher) throws IOException, SecurityException, InterruptedException {
        closeConnection();
        mSerialPort=mSerialPortModule.openSerialPort(path,bdRate);
//        mRevTread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isInterrupted=false;
//                mInputStream = mSerialPort.getInputStream();
//                while (!isInterrupted){
//                    showLog("reading from input stream...");
//                    try {
//                        if(mInputStream.available()>0){
//                            int len = mInputStream.read(mPortData);
//                            if(len>0){
//                                Log.e("Port Data",new String(mPortData,0,len));
//                                try {
//                                    ackDecipher.decipherByBytes(mPortData,len);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                    break;
//                                }
//                            }
//                        }
//                        try {
//                            Thread.sleep(300);//休息300毫秒，避免频繁读取串口
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (IOException e) {
//                        showLog("IOException:"+e.getMessage());
//                        break;
//                    }
//                }
//            }
//        });
//        mRevTread.start();
        mAckDecipher=ackDecipher;
        mAckDecipher.decipherByStream(mSerialPort.getInputStream());
    }


    public void closeConnection() throws InterruptedException {
        if(mSerialPort!=null){
//            if(mRevTread!=null&&mRevTread.isAlive()){
//                isInterrupted=true;
//                mRevTread.join();
//            }
            if(mAckDecipher!=null){
                mAckDecipher.stopDecipherByStream();
            }
            mSerialPortModule.close(mSerialPort);
            mSerialPort=null;
        }
    }

    public void testConnection() throws IOException {
        getInstance().testConnection(mSerialPort.getOutputStream());
    }
    public void reset() throws IOException {
        getInstance().reset(mSerialPort.getOutputStream());
    }
    public void getSysTime() throws IOException {
        getInstance().getSysTime(mSerialPort.getOutputStream());
    }
    public void setBaudRate(String uart,String bdRate) throws IOException, InputFormatException,NumberFormatException {
        getInstance().setBaudRate(
                mSerialPort.getOutputStream(),
                uart,
                bdRate
        );

    }
    public void getBaudRate() throws Exception {
        throw new Exception("此功能未开通！");
    }
    public void setFreq(String freq) throws IOException,NumberFormatException {
        getInstance().setFreq(mSerialPort.getOutputStream(),freq);
    }
    public void getFreq() throws IOException {
        getInstance().getFreq(mSerialPort.getOutputStream());

    }
    public void setReceiveMode(String mode) throws IOException, InputFormatException,NumberFormatException {
        getInstance().setReceiveMode(mSerialPort.getOutputStream(),mode);

    }
    public void getReceiveMode() throws IOException {
        getInstance().getReceiveMode(mSerialPort.getOutputStream());

    }
    public void setRunningMode(String mode) throws IOException, InputFormatException,NumberFormatException {
        getInstance().setRunningMode(mSerialPort.getOutputStream(),mode);
    }
    public void getRunningMode() throws IOException {
        getInstance().getRunningMode(mSerialPort.getOutputStream());

    }
    public void toggleCKFO(boolean isToOpen) throws IOException {
        getInstance().toggleCKFO(mSerialPort.getOutputStream(),isToOpen);
    }
    public void checkIsOpenCKFO() throws IOException {
        getInstance().checkIsOpenCKFO(mSerialPort.getOutputStream());

    }
    public void toggle1PPS(boolean isToOpen) throws IOException {
        getInstance().toggle1PPS(mSerialPort.getOutputStream(),isToOpen);

    }
    public void checkIsOpen1PPS() throws IOException {
        getInstance().checkIsOpen1PPS(mSerialPort.getOutputStream());
    }
    public void beginSysUpgrade() throws IOException {
        getInstance().beginSysUpgrade(mSerialPort.getOutputStream());
    }
    public void getSysVersion() throws IOException {
        getInstance().getSysVersion(mSerialPort.getOutputStream());

    }
    public void setChipId(String id) throws IOException, InputFormatException {
        getInstance().setChipId(mSerialPort.getOutputStream(),id);

    }
    public void getChipId() throws IOException {
        getInstance().getChipId(mSerialPort.getOutputStream());

    }
    public void getSNR() throws IOException {
        getInstance().getSNR(mSerialPort.getOutputStream());

    }
    public void getSysState() throws IOException {
        getInstance().getSysState(mSerialPort.getOutputStream());

    }
    public void getCFO() throws IOException {
        getInstance().getCFO(mSerialPort.getOutputStream());

    }
    public void getSFO() throws IOException {
        getInstance().getSFO(mSerialPort.getOutputStream());

    }
    public void getTunerState() throws IOException {
        getInstance().getTunerState(mSerialPort.getOutputStream());

    }
    public void getLDPC() throws IOException {
        getInstance().getLDPC(mSerialPort.getOutputStream());

    }
    public void setLogLevel(String level) throws IOException, InputFormatException,NumberFormatException {
        getInstance().setLogLevel(mSerialPort.getOutputStream(),level);

    }

    private void showLog(String msg){
        LogUtils.e(getClass().getSimpleName(),msg);
    }

}
