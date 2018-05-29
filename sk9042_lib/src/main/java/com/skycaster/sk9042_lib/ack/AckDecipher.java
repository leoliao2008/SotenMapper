package com.skycaster.sk9042_lib.ack;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.skycaster.sk9042_lib.Static;
import com.skycaster.sk9042_lib.request.RequestManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 廖华凯 on 2018/3/16.
 * 一个专门用来解析SK9042发回来的ACK的类
 */

public class AckDecipher {
    private RequestCallBack mCallBack;
    private volatile boolean isAckConfirmed;
    private byte[] ack=new byte[Static.ACK_SIZE];
    private int mIndex;
    private Thread mRevThread;
    private volatile boolean isInterrupted;
    private byte[] temp=new byte[Static.ACK_SIZE];




    //**********************************************函数****************************************
    public AckDecipher(RequestCallBack callBack) {
        mCallBack = callBack;
    }


    /**
     * 以串口输入流作为参数，自动新建一条线程解析串口数据，在子线程中调用回调返回结果
     * @param inputStream 串口输入流
     * @throws InterruptedException
     */
    public synchronized void decipherByStream(final InputStream inputStream) throws InterruptedException {
        stopDecipherByStream();
        mRevThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isInterrupted=false;
                while (!isInterrupted){
                    try {
                        int available = inputStream.available();
                        if(available >0){
                            int read = inputStream.read(temp);
                            try {
                                decipherByBytes(temp,read);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        mRevThread.start();
    }

    /**
     * 停止解析串口数据，释放资源。
     * @throws InterruptedException
     */
    public void stopDecipherByStream() throws InterruptedException {
        if(mRevThread!=null&&mRevThread.isAlive()){
            isInterrupted=true;
            mRevThread.join();
        }
    }

    /**
     * 以CDRadio模块串口发过来的数据包作为参数解析当前数据。
     * @param data CDRadio模块串口发过来的数据包
     * @param len 数据包有效字节长度
     * @throws InterruptedException
     */
    public synchronized void decipherByBytes(byte[] data, int len) throws InterruptedException {
        for (int i = 0; i < len; i++) {
            if (isAckConfirmed) {
                ack[mIndex] = data[i];
                if (data[i] == '\n' && mIndex > 0 && ack[mIndex - 1] == '\r') {
                    //监听到了ack的结束符，开始解析
                    decipherBySk9042Protocol(ack, mIndex);
                    //复位
                    mIndex = 0;
                    isAckConfirmed = false;
                    continue;
                }
                mIndex++;
            }
            //监听到了ack的起始符，开始记录
            if (data[i] == '+') {
                ack[mIndex++] = data[i];
                isAckConfirmed = true;
            }
        }
    }


    /**
     * 具体的解析函数，根据Sk9042串口通讯协议Ver 1.5.0编写。
     * @param ack ack的容器
     * @param len 有效长度
     */
    private void decipherBySk9042Protocol(byte[] ack, int len) {
        showLog("begin to decipherByBytes...");
//        showLog(hexToString(ack,len));
        //把“+”号去掉，把后面的换行符给trim掉
        String trim = new String(ack, 0, len).substring(1, len).trim();
        showLog("raw ack:"+new String(ack, 0, len));
        showLog("remove + and \\r\\n: "+trim);
        String[] split=null;
        if(trim.contains("=")){
            //如果ack中包含“=”号，就按等号分开2段。
            split = trim.split("=");
        }else if(trim.contains(":")){
            //如果ack中包含“：”号，就按分号分开2段。
            split = trim.split(":");
        }else if(trim.equals("OK")){
            mCallBack.testConnection(true);
        }
//        //测试用，正式删
//        if(split!=null){
//            for(String s:split){
//                showLog("split:"+s);
//            }
//        }else {
//            showLog("split == null!");
//        }

        if(split!=null&&split.length>0){
            switch (split[0]){
                case "RESET":
                    if(split[1].equals("OK")){
                        mCallBack.reset(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.reset(false);
                    }
                    break;
                case "GET_TIME":
                    mCallBack.getSysTime(split[1]);
                    break;
                case "BDRT":
                    if(split[1].equals("OK")){
                        mCallBack.setBaudRate(true);
                    }else if (split[1].equals("ERROR")){
                        mCallBack.setBaudRate(false);
                    }else {
                        try {
                            int i=Integer.valueOf(split[1]);//先测试能否转成整数
                            mCallBack.getBaudRate(true,split[1]);
                        }catch (NumberFormatException e){
                            mCallBack.getBaudRate(false,split[1]);
                        }
                    }
                    break;
                case "FREQ":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.setFreq(true);
                        }else if(split[1].equals("ERROR")){
                            mCallBack.setFreq(false);
                        }
                    }else if(trim.contains(":")){
                        if(split[1].equals("NONE")){
                            mCallBack.getFreq(false,null);
                        }else {
                            mCallBack.getFreq(true,split[1]);
                        }
                    }
                    break;
                case "RMODE":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.setReceiveMode(true);
                        }else if(split[1].equals("ERROR")){
                            mCallBack.setReceiveMode(false);
                        }
                    }else if(trim.contains(":")){
                        mCallBack.getReceiveMode(split[1]);
                    }
                    break;
                //此功能在1.4.2中被删除
//                case "RM":
//                    if(trim.contains("=")){
//                        if(split[1].equals("OK")){
//                            mCallBack.setRunningMode(true);
//                        }else if(split[1].equals("ERROR")){
//
//                            mCallBack.setRunningMode(false);
//                        }
//                    }else if(trim.contains(":")){
//                        mCallBack.getRunningMode(split[1]);
//                    }
//                    break;
                case "CKFO":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.toggleCKFO(true);
                        }else if(split[1].equals("ERROR")){
                            mCallBack.toggleCKFO(false);
                        }
                    }else if(trim.contains(":")){
                        mCallBack.checkIsOpenCKFO(split[1]);
                    }
                case "1PPS":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.toggle1PPS(true);
                        }else if(split[1].equals("ERROR")){
                            mCallBack.toggle1PPS(false);
                        }
                    }else if(trim.contains(":")){
                        mCallBack.checkIsOpen1PPS(split[1]);
                    }
                    break;
                case "SVER":
                    mCallBack.getSysVersion(split[1]);
                    break;
                case "ID":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.setChipId(true);
                        }else if(split[1].equals("ERROR")){
                            mCallBack.setChipId(false);
                        }
                    }else if(trim.contains(":")){
                        mCallBack.getChipId(split[1]);
                    }
                    break;
                case "GET_SNR":
                    mCallBack.getSNR(split[1]);
                    break;
                case "STAT":
                    mCallBack.getSysState(split[1]);
                    break;
                case "GET_SFO":
                    mCallBack.getSFO(split[1]);
                    break;
                case "GET_CFO":
                    mCallBack.getCFO(split[1]);
                    break;
                case "GET_TUNER":
                    if(split[1].equals("OK")){
                        mCallBack.getTunerState(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.getTunerState(false);
                    }
                    break;
                case "GET_LDPC":
                    String[] spl2 = split[1].split(",");
                    if(spl2.length==2){
                        mCallBack.getLDPC(spl2[0],spl2[1]);
                    }
                    break;
                case "LOG":
                    if(split[1].equals("OK")){
                        mCallBack.setLogLevel(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.setLogLevel(false);
                    }
                    break;
                case "SEARCH":
                    if(split[1].equals("QUIT")){//搜台被终止
                        mCallBack.stopSearchFreq();
                    }else if(split[1].equals("ERROR")){//搜台完毕，但是没有找到可用频点
                        mCallBack.startSearchFreq(false,null);
                    }else {
                        try {
                            Integer temp = Integer.valueOf(split[1]);//检查一下能否转换成数字
                            mCallBack.startSearchFreq(true,split[1]);//确定没问题后把最终结果返回
                        }catch(NumberFormatException e){
                            mCallBack.startSearchFreq(false,null);//如果没法转成数字，还是以没有找到可用频点的形式返回
                        }
                    }
                    break;
                case "CHECK"://频点检查
                    //+CHECK=OK\r\n
                    //+CHECK=ERROR\r\n
                    if(split[1].equals("OK")){
                        mCallBack.verifyFreq(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.verifyFreq(false);
                    }
                    break;
                case "UDRC"://升级第一步：已接收到升级指令
                    if(split[1].equals("OK")){
                        mCallBack.onConfirmUpgradeStart();
                    }
                    break;
                case "UDSEND"://升级第二步：表示已经复位成功，用户可以开始发送bin文件
                    if(split[1].equals("OK")){
                        commenceUpgrade();
                    }
                    break;
                case "STUD"://升级第三步：升级结果的回调
                    if(split[1].equals("OK")){
                        mCallBack.onUpgradeFinish(true,"升级成功。");
                    }else if(split[1].contains("ERROR")){
                        String[] strings = split[1].split("ERROR");
                        String error=null;
                        if(strings.length>1){
                            switch (strings[1]){
                                case "1":
                                    error="升级超时。";
                                    break;
                                case "2":
                                    error="bin文件长度校验失败。";
                                    break;
                                case "3":
                                    error="bin文件CRC校验失败。";
                                    break;
                                case "4":
                                    error="bin文件超长（最大58K）。";
                                    break;
                                default:
                                    error="未知原因。";
                                    break;
                            }
                        }
                        mCallBack.onUpgradeFinish(false,error);
                    }
                    break;
                default:
                    break;
            }
        }


    }


    /**
     * 通过发送升级文件，升级sk9042系统
     */
    private void commenceUpgrade() {
        //在子线程中完成
        showLog("commenceUpgrade");
        new Thread(new Runnable() {
            @Override
            public void run() {
                showLog("upgrade thread starts.");
                byte[] temp=new byte[128];
                int read=-1;
                Handler handler=new Handler(Looper.getMainLooper());
                try {
                    FileInputStream inputStream=new FileInputStream(RequestManager.getSysUpgradeSrcFile());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallBack.onStartTransferringUpgradeFile();
                        }
                    });
                    while ((read=inputStream.read(temp))>0){
                        RequestManager.getSysUpgradeOstream().write(temp,0,read);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallBack.onFinishTransferringUpgradeFile();
                        }
                    });
                } catch (Exception e) {
                    showLog(e.getMessage());
                }
                showLog("upgrade thread ends.");
            }
        }).start();
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

}
