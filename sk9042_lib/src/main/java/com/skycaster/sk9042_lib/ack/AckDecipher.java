package com.skycaster.sk9042_lib.ack;


import android.util.Log;

import com.skycaster.sk9042_lib.Static;

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
     * 以串口输入流作为参数，自动新建一条线程解析串口数据
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
     * 具体的解析函数，根据Sk9042协议编写。
     * @param ack ack的容器
     * @param len 有效长度
     */
    private void decipherBySk9042Protocol(byte[] ack, int len) {
        showLog("begin to decipherByBytes...");
//        showLog(hexToString(ack,len));
        //把“+”号去掉，把后面的换行符给trim掉
        String trim = new String(ack, 0, len).substring(1, len).trim();
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
                case "BDRT ":
                    if(split[1].equals("OK")){
                        mCallBack.setBaudRate(true);
                    }else if (split[1].equals("ERROR")){
                        mCallBack.setBaudRate(false);
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
                case "RM":
                    if(trim.contains("=")){
                        if(split[1].equals("OK")){
                            mCallBack.setRunningMode(true);
                        }else if(split[1].equals("ERROR")){

                            mCallBack.setRunningMode(false);
                        }
                    }else if(trim.contains(":")){
                        mCallBack.getRunningMode(split[1]);
                    }
                    break;
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
                    String[] spl1 = split[1].split(",");
                    if(spl1.length==2){
                        mCallBack.getTunerState(spl1[0],spl1[1]);
                    }
                    break;
                case "GET_LDPC":
                    String[] spl2 = split[1].split(",");
                    for(String temp:spl2){
                        showLog("XXXXXXXXXXX:"+temp);
                    }
                    if(spl2.length==2){
                        mCallBack.getLDPC(spl2[0],spl2[1]);
                    }else {
                        showLog("spl2 lengh = "+spl2.length);
                    }
                    break;
                case "LOG":
                    if(split[1].equals("OK")){
                        mCallBack.setLogLevel(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.setLogLevel(false);
                    }
                    break;
                case "STUD":
                    if(split[1].equals("OK")){
                        mCallBack.setLogLevel(true);
                    }else if(split[1].equals("ERROR")){
                        mCallBack.setLogLevel(false);
                    }
                    break;
                default:
                    break;
            }
        }


    }

    private String hexToString(byte[] tempBytes,int len) {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<len;i++){
            sb.append("0x").append(Integer.toHexString(tempBytes[i])).append(" ");
        }
        return sb.toString();
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
