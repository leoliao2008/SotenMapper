package com.skycaster.gps_decipher_lib;

import android.util.Log;

import com.skycaster.gps_decipher_lib.GPGGA.GPGGABean;
import com.skycaster.gps_decipher_lib.GPGGA.TbGNGGABean;
import com.skycaster.gps_decipher_lib.GPGSA.GPGSABean;
import com.skycaster.gps_decipher_lib.GPGSV.GPGSVBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * Created by 廖华凯 on 2017/6/7.
 * 一个用来解析卫星定位裸数据的类，可以从字符串中提取GPGGA数据，GPGSA数据，GPGSV数据，这些数据可以用来显示地理位置，绘制卫星图等。
 * 注意，卫星定位裸数据是有规定格式的，如"$GPGGA,041837.00,2234.2885967,N,11356.2338380,E,4,18,1.7,19.7801,M,-3.513,M,01,0004*7C"，这个库是参考了他们的协议写的。
 */

public class GPSDataExtractor {
    private static int DATA_LEN=512;
    private static byte[] temp=new byte[DATA_LEN];
    private static byte[] GPSData =new byte[DATA_LEN];
    private static AtomicBoolean isExtractingGPSData =new AtomicBoolean(false);
    private static boolean isDataHeadConfirmed;
    private static int index;
    private GPSDataExtractor(){}


    /**
     * 把串口输入流作为参数，在子线程中提取卫星数据，注意，回调是在子线程中返回的。结束解析数据后，必须手动执行stopExtractingGPGGADataByStream()
     * 这个函数，否则子线程不会中断。
     * @param inputStream 串口输入流
     * @param callBack 回调
     */
    public static synchronized void startExtractingGPSDataByStream(final InputStream inputStream, final GPSDataExtractorCallBack callBack){
        isExtractingGPSData.compareAndSet(false,true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream.skip(9999);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (isExtractingGPSData.get()){
                    try {
//                        showLog("InputStream Available:"+inputStream.available());
                        int len = inputStream.read(temp);
//                        showLog("len="+len);
                        // $GPGGA,235949.042,0000.0000,N,00000.0000,E,0,00,,0.0,M,0.0,M,,0000*45
                        if(len<1){
                            continue;
                        }
                       decipherData(temp,len,callBack);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 停止通过串口输入流解析卫星数据
     * @return 执行结果
     */
    public static synchronized boolean stopExtractingGPGGADataByStream(){
        return isExtractingGPSData.compareAndSet(true,false);
    }

    /**
     * 直接把数据包作为参数，解析里面的卫星定位数据
     * @param temp
     * @param len
     * @param callBack
     */
    public static synchronized void decipherData(byte[] temp, int len, final GPSDataExtractorCallBack callBack){
        for(int i=0;i<len;i++){
            if(!isDataHeadConfirmed){
                // showLog("Ack Head not confirmed.");
                if(temp[i]=='$'){
                    isDataHeadConfirmed =true;
                    //showLog("Ack Head is confirmed!");
                    index=0;
                    GPSData[index++]=temp[i];
                }else {
//                                    char c = (char) temp[i];
//                                    showLog(String.valueOf(c)+"is forfeit.");
                }
            }else {
                if(temp[i]=='$'){
//                                    showLog("tail is reached.");
                    byte[] clone = GPSData.clone();
//                                    showHint(toHexString(clone,index));
                    String source = new String(clone, 0, index);
//                    showLog("Source Get: "+source);
                    if(source.contains("GNGGA")){//新增专门解析天宝北斗模块的逻辑
                        //天宝的比较特殊，不用校验了，直接使用
                        callBack.onGetTBGNGGABean(new TbGNGGABean(source));
                    }else if(source.contains("GPGGA")){
                        if(isSourceValid(source)){//先校验一下
                            callBack.onGetGPGGABean(new GPGGABean(source));
                        }else {
                            showLog("GPGGA data is not valid. Data is abandon.");
                        }
                    }else if(source.contains("GPGSV")){
                        if(isSourceValid(source)){//先校验一下
                            GPGSVBean.getInstance().decipher(source,callBack);
                        }else {
                            showLog("GPGSV data is not valid. Data is abandon.");
                        }
                    }else if(source.contains("GPGSA")||source.contains("GNGSA")){
//                        if(isSourceValid(source)){//先校验一下
//                            callBack.onGetGPGSABean(new GPGSABean(source));
//                        }else {
//                            showLog("GPGSA data is not valid. Data is abandon.");
//                        }
                        //不用校验了，直接使用
                        callBack.onGetGPGSABean(new GPGSABean(source));
                    }
                    index=0;
                }
                GPSData[index++]=temp[i];
                if(index==DATA_LEN-1){
                    index=0;
                    showLog("data len exceed boundary, reset index to 0");
                }
            }
        }
    }

    /**
     * 校验裸数据是否符合格式
     * @param source
     * @return
     */
    private static synchronized boolean isSourceValid(String source) {
        showLog("begin to check if source valid...:"+source);
        int checkSum=0;
        String[] data = source.split(Pattern.quote("*"));
        if(data.length==2){
            String[] dataCheck = data[0].split(Pattern.quote("$"));
            for(String s:dataCheck){
                showLog("dataCheck: "+s);
            }
            if(dataCheck.length==2){
                char[] chars = dataCheck[1].toCharArray();
                for(char c:chars){
                    checkSum^=c;
                }
                showLog("check sum="+ String.format("%X",checkSum)+" vs source ="+data[1].trim());
                return Integer.toHexString(checkSum).equalsIgnoreCase(data[1].trim());
            }
        }
        return false;
    }

    private static void showLog(String msg) {
        Log.e("GPSDataExtractor",msg);
    }






}
