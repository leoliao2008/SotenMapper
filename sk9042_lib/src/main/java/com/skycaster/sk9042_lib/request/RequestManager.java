package com.skycaster.sk9042_lib.request;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 专门用来向SK9042模块发送指令的管理类
 */
public class RequestManager {
    private static volatile RequestManager requestManager;
    private String mAppendix="\r\n";
    private RequestManager(){}
    private volatile AtomicBoolean isSearchingFreq=new AtomicBoolean(false);
    private static File sysUpgradeSrcFile;
    private static OutputStream sysUpgradeOstream;

    /**
     * 以单例模式获得该类实例
     * @return 该类实例
     */
    public synchronized static RequestManager getInstance(){
        if(requestManager==null){
            requestManager=new RequestManager();
        }
        return requestManager;
    }

    /**
     * 测试SK9042模块和平板的连接状态
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void testConnection(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.TEST_CONN);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当算法出现某种异常情况的稳定运行状态，可以选择发送此AT指令进行算法复位。算法复位不会对系统设置产生影响。
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void reset(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.RESET);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得日期及时间，算法API提供。返回格式：如:2017,03,05,14,10,05 表示2017年3月5日14点10分5秒
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSysTime(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_TIME);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置波特率。串口1为数据通道，默认波特率为115200。串口2为控制通道，默认波特率为57600。
     *串口1和串口2的波特率必须单独设置，如果波特率设置返回错误，应该排查以下情况：
     *1、命令格式是否正确。
     *2、波特率参数只接受4个值：9600、57600，19200、115200。
     * @param os 串口输出流
     * @param bdRate 用ASCII字符串表示，如:9600,即设置串口波特率为9600. bdRate值可以是9600、57600，19200、115200
     * @throws IOException 串口输出流报错
     * @throws NumberFormatException 当参数不符合上述规定时报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setBaudRate(OutputStream os,String bdRate) throws IOException,NumberFormatException,InputFormatException {
        sendRequest(os, RequestType.SET_BD_RATE,bdRate);
    }

    /**
     * 设置频点。频点的设置注意以下三点：
     *1、在查询频点之前，如果没有手动设置频点，并且没有经过搜台，会返回NONE值。
     *2、设置的频点数据会写入flash。
     * @param os 串口输出流
     * @param freq 频点，用ASCII字符串表示，如:9800,即设置频点为98MHz（Tuner以10KHz为单位）。频点的取值范围为[8790-10800]，超出此范围会报错。
     * @throws NumberFormatException 当参数不符合上述规定时报错
     * @throws IOException 串口输出流报错
     */
    public synchronized void setFreq(OutputStream os,String freq) throws IOException,NumberFormatException{
        int value=Integer.valueOf(freq);
        if(value<8790||value>10800){
            throw new NumberFormatException("The value of freq must be within the boundary of [8790-10800]!");
        }else {
            try {
                sendRequest(os, RequestType.SET_FREQ,freq);
            } catch (InputFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询频点
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getFreq(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_FREQ);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置接收模式。用户设置接收模式后，接收模式会立即生效。但是因为算法在进行快搜时，无法及时响应AT指令，所以用户设置接收模式后，可能会表现出一些延迟。
     * 模式信息会写flash，算法下次启动时，会从flash中读取模式信息。即算法会运行在上次用户设置的模式上。如果用户从来没有设置过接收模式，会运行在模式2。
     * @param os 串口输出流
     * @param mode 用ASCII字符串表示：2：模式2；3：模式3；4：模式4；
     * @throws IOException 串口输出流报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     * @throws NumberFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setReceiveMode(OutputStream os, String mode) throws IOException,InputFormatException,NumberFormatException{
        sendRequest(os, RequestType.SET_REV_MODE,mode);
    }

    /**
     * 查询当前接收模式
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getReceiveMode(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_REV_MODE);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }


    //以下功能在1.4.2版被删除
//    /**
//     * 设置运行模式。模式3为默认运行模式。
//     * @param os 串口输出流
//     * @param mode 用ASCII字符串表示：
//     *1：模式1：采用用户设置的固定的频点，使用此模式用户必须先设置频点
//     *2：模式2：进行自动搜台模式，并将可用的频点信息写入到flash
//     *3：模式3：采用flash中保存的频点，如果flash中没有保存频点数据，或者flash中的频点数据不能解数据，自动切换到模式2进行搜台
//     * @throws IOException 串口输出流报错
//     * @throws InputFormatException 当参数不符合上述规定时报错
//     * @throws NumberFormatException 当参数不符合上述规定时报错
//     */
//    public synchronized void setRunningMode(OutputStream os,String mode) throws IOException, InputFormatException,NumberFormatException {
//        sendRequest(os, RequestType.SET_RUN_MODE,mode);
//    }
//
//    /**
//     * 查询当前运行模式
//     * @param os 串口输出流
//     * @throws IOException 串口输出流报错
//     */
//    public synchronized void getRunningMode(OutputStream os) throws IOException {
//        try {
//            sendRequest(os, RequestType.GET_RUN_MODE);
//        } catch (InputFormatException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 设置数据输出校验功能
     * @param os 串口输出流
     * @param isToOpen  用ASCII字符串表示；
     * true：使能。校验失败不输出。
     * false：禁止。不校验输出。
     * @throws IOException 串口输出流报错
     */
    public synchronized void toggleCKFO(OutputStream os,boolean isToOpen) throws IOException{
        try {
            sendRequest(os, RequestType.TOGGLE_CKFO,isToOpen?"ENABLE":"DISABLE");
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询当前数据输出校验功能是否已经打开
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void checkIsOpenCKFO(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_CKFO);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置1PPS开关。
     * @param os 串口输出流
     * @param isToOpen  用ASCII字符串表示；
     * true： 1PPS开启
     * false： 1PPS关闭
     * @throws IOException 串口输出流报错
     */
    public synchronized void toggle1PPS(OutputStream os,boolean isToOpen) throws IOException{
        try {
            sendRequest(os, RequestType.TOGGLE_1PPS,isToOpen?"OPEN":"CLOSE");
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询当前1PPS是否已经开启。
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void checkIsOpen1PPS(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_1PPS);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 启动系统升级。
//     * @param os 串口输出流
//     * @param srcFile 升级文件
//     * @throws IOException 串口输出流报错
//     */
//    public synchronized void beginSysUpgrade(OutputStream os,File srcFile) throws IOException{
//        try {
//            sendRequest(os, RequestType.SYS_UPGRADE_START);
//        } catch (InputFormatException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 查询SK9042模块当前系统版本。
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSysVersion(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_VERSION);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部使用。出厂时使用该指令设置SK9042模块的ID。
     * @param os 串口输出流
     * @param id 用ASCII字符串表示，最大长度不超过20。如:123
     * @throws IOException 串口输出流报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setChipId(OutputStream os,String id) throws IOException, InputFormatException {
        sendRequest(os, RequestType.SET_CHIP_ID,id);
    }

    /**
     * 获取SK9042模块的ID
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getChipId(OutputStream os) throws IOException {
        try {
            sendRequest(os, RequestType.GET_CHIP_ID);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询信噪比
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSNR(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_SNR);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询接收状态
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSysState(OutputStream os) throws IOException {
        try {
            sendRequest(os, RequestType.GET_SYS_STATE);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询时偏
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSFO(OutputStream os) throws IOException {
        try {
            sendRequest(os, RequestType.GET_SFO);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询频偏
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getCFO(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_CFO);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询tuner状态,内部调试使用
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getTunerState(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_TUNER);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取译码统计
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getLDPC(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.GET_LDPC);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log等级设置
     * @param os 串口输出流
     * @param level 表示log等级：
     * 0：关闭一切显示
     * 1：用户信息，打印必要的交互信息以指示系统运行状态
     * 2：致命错误，导致程序死机
     * 3：运行错误，系统可以正常运行，但是结果不对
     * 4：警告，可以正常运行，可能导致错误的信息
     * 5：调试信息
     * @throws IOException 串口输出流报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     * @throws NumberFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setLogLevel(OutputStream os,String level) throws IOException, InputFormatException,NumberFormatException {
        sendRequest(os, RequestType.SET_LOG_LEVEL,level);
    }

    /**
     * 初始化SK9042系统升级
     * @param os os 串口输出流
     * @param srcFile 升级文件本地路径
     * @throws IOException 串口输出流报错
     */
    public synchronized void startUpgrade(OutputStream os,File srcFile) throws IOException {
        try {
            //这里提取两个参数出来，要给AckDecipher升级时调用
            sysUpgradeSrcFile=srcFile;
            sysUpgradeOstream=os;
            sendRequest(os,RequestType.SYS_UPGRADE_START,srcFile);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始搜台。启动搜台后，AT指令的响应速度会受到影响。
     * @param os os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void startMatchFreq(OutputStream os) throws IOException{
        if(!isSearchingFreq.get()){
            try {
                sendRequest(os,RequestType.AUTO_MATCH_FREQ);
                isSearchingFreq.set(true);
            } catch (InputFormatException e) {
                e.printStackTrace();//绝对不会发生
            }
        }
    }

    /**
     * 停止搜台
     * @param os os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void stopMatchFreq(OutputStream os) throws IOException{
        if(isSearchingFreq.get()){
            try {
                sendRequest(os,RequestType.AUTO_MATCH_FREQ);
                isSearchingFreq.set(false);
            } catch (InputFormatException e) {
                e.printStackTrace();//绝对不会发生
            }
        }
    }

    /**
     * 判断该频点是否被SK9042支持
     * @param os os串口输出流
     * @param freq 频点值，比如说9800（代表98M）
     * @throws IOException 串口输出流报错
     */
    public synchronized void verifyFreq(OutputStream os,String freq) throws IOException{
        try {
            sendRequest(os,RequestType.VERIFY_FREQ,freq);
        } catch (InputFormatException e) {
            e.printStackTrace();//绝对不会发生
        }
    }

    /**
     * 查询SK9042模块差分数据输出端串口的波特率
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getBaudRate(OutputStream os) throws IOException {
        try {
            sendRequest(os,RequestType.GET_BD_RATE);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }



    /**
     * 发送指令给SK9042模块的底层函数
     * @param os 串口输出流
     * @param rq 请求类型，一个枚举变量，可以直接转成字符串合成指令。
     * @param params 参数，可选
     * @throws IOException 串口输出流报错
     * @throws InputFormatException 当参数不协议时报错
     * @throws NumberFormatException 当参数不协议时报错
     */
    private synchronized void sendRequest(OutputStream os, RequestType rq, Object... params) throws IOException, InputFormatException,NumberFormatException {
        StringBuilder sb=new StringBuilder();
        switch (rq){
            case TEST_CONN:
            case RESET:
            case GET_FREQ:
            case GET_REV_MODE:
            case GET_CKFO:
            case GET_1PPS:
            case GET_TIME:
            case GET_VERSION:
            case GET_CHIP_ID:
            case GET_SNR:
            case GET_SYS_STATE:
            case GET_SFO:
            case GET_CFO:
            case GET_TUNER:
            case GET_LDPC:
            case AUTO_MATCH_FREQ:
            case GET_BD_RATE:
                sb.append(rq.toString()).append(mAppendix);
                break;
            case SET_BD_RATE:
                Integer bdRate=Integer.valueOf((String)params[0]);
                if(bdRate==9600||bdRate==57600||bdRate==19200||bdRate==115200){
                    sb=buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Serial port baud rate is confined to 9600, 57600, 19200, 115200.");
                }
                break;
            case SET_FREQ:
                Integer.valueOf((String)params[0]);//先检查一下字符串是否能转成数字。
                sb= buildCmdWithParam0(rq,params[0]);
                break;
            case SET_REV_MODE:
                Integer i5=Integer.valueOf((String)params[0]);
                if(i5==2||i5==3||i5==4){
                    sb= buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Receive mode is confined to 2,3 or 4.");
                }
                break;
            case TOGGLE_CKFO:
                if(params[0].equals("ENABLE")|| params[0].equals("DISABLE")){
                    sb= buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("The param is confined only to \"ENABLE\" or \"DISABLE\"");
                }
                break;
            case TOGGLE_1PPS:
                if(params[0].equals("OPEN")|| params[0].equals("CLOSE")){
                    sb= buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("The param is confined only to \"OPEN\" or \"CLOSE\"");
                }
                break;
            case SYS_UPGRADE_START:
                sb.append(RequestType.SYS_UPGRADE_START);
                File src= (File) params[0];
                sb.append(genVerifyCode(src)).append(",").append(src.length()).append(mAppendix);
                showLog("System upgrade command: "+sb.toString());
                break;
            case SET_CHIP_ID:
                String t1= (String) params[0];
                if(t1.length()<=20){
                    sb=buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("The length of the ID must be no less than 20.");
                }
                break;
            case SET_LOG_LEVEL:
                Integer i2 = Integer.valueOf((String) params[0]);
                if(i2>=0&&i2<=5){
                    sb=buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Log level must be within the range of [0-5].");
                }
                break;
            case VERIFY_FREQ:
                Integer i4 = Integer.valueOf((String) params[0]);
                sb.append(rq.toString()).append(":").append(i4).append(mAppendix);
                break;
            default:
                break;
        }
        showLog("request send: "+sb.toString());
        os.write(sb.toString().getBytes());
    }

    /**
     * 根据SK9042开发人员提供的C源码编写的生成升级文件校验码
     * @param src 升级文件
     * @return 效验码，以“0xffff”的形式返回。
     * @throws IOException 文件读取失败弹出的异常
     */
    private String genVerifyCode(File src) throws IOException {
        int len = (int) (src.length()>Integer.MAX_VALUE?Integer.MAX_VALUE:src.length());
        //定义一个和升级文件同样大小的字节数组
        byte[] bytes=new byte[len];
        FileInputStream fStream=null;
        try{
            fStream=new FileInputStream(src);
            //把升级文件复制到字节数组中去，为了避免和SK9042端生成的结果不一致，这里用最笨的办法，仿照SK9042开发人员的写法。
            for(int i=0;i<len;i++){
                bytes[i]=(byte) fStream.read();
            }
            //利用CRC-CCITT多项式（？？？）的方式生成升级校验码。
            int result = processData(bytes);
            //以“0xffff”的格式返回该校验码的字符串形式。
            return String.format("%#x", result);
        } finally {
            if(fStream!=null){
                fStream.close();
                fStream=null;
            }
        }
    }

    /**
     * 利用CRC-CCITT多项式（？？？）的方式生成最终校验码。
     * @param data 代表升级文件的字节数组
     * @return 升级校验码
     */
    private int processData(byte[]data){
        int result=0;
        for(byte temp:data){
            for(int i=0x80;i!=0;i=i>>1){
                result= result*2;
                if((result&0x10000)!=0){
                    result=result^0x11021;
                }
                if((temp&i)!=0){
                    result=result^(0x10000^0x11021);
                }
            }
        }
        return result;
    }


    /**
     * 利用参数拼接发送给SK9042模块的指令
     * @param rq 请求类型
     * @param param 参数，只能有一个
     * @return 拼接完成后的指令
     */
    private synchronized StringBuilder buildCmdWithParam0(RequestType rq, Object param) {
        return new StringBuilder().append(rq.toString()).append("=").append(param).append(mAppendix);
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    /**
     * 获取SK9042升级源文件，供SDK内部调用，开发者可以忽略这个函数。
     * @return 升级源文件
     */
    public static File getSysUpgradeSrcFile() {
        return sysUpgradeSrcFile;
    }

    /**
     * 获取SK9042升级数据通道，供SDK内部调用，开发者可以忽略这个函数。
     * @return 输出流
     */
    public static OutputStream getSysUpgradeOstream() {
        return sysUpgradeOstream;
    }
}
