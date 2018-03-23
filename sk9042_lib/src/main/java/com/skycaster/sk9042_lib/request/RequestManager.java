package com.skycaster.sk9042_lib.request;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 专门用来向SK9042模块发送指令的管理类
 */
public class RequestManager {
    private static volatile RequestManager requestManager;
    private String mAppendix="\r\n";
    private RequestManager(){}

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
            sendRequest(os, RequestType.TIME);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置波特率。串口1为数据通道，默认波特率为115200。串口2为控制通道，默认波特率为57600。
     *串口1和串口2的波特率必须单独设置，如果波特率设置返回错误，应该排查以下情况：
     *1、命令格式是否正确。
     *2、串口号参数uart只接受两个值：1和2。
     *3、波特率参数只接受4个值：9600、57600，19200、115200。
     * @param os 串口输出流
     * @param uart 用ASCII字符串表示,uart的值可以是1或者2，表示对第一路串口或第二路串口进行设置
     * @param bdRate 用ASCII字符串表示，如:9600,即设置串口波特率为9600. bdRate值可以是9600、57600，19200、115200
     * @throws IOException 串口输出流报错
     * @throws NumberFormatException 当参数不符合上述规定时报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setBaudRate(OutputStream os,String uart,String bdRate) throws IOException,NumberFormatException,InputFormatException {
        sendRequest(os, RequestType.SET_BD_RATE,uart,bdRate);
    }

    /**
     * 设置频点。频点的设置注意以下两点：
     *1、在查询频点之前，如果没有手动设置频点，并且没有经过搜台，会返回NONE值。
     *2、如果在模式1，设置频点之后会立即生效。如果在模式2和模式3，设置频点之后不能立即生效。
     *3、设置的频点数据会写入flash。
     * @param os 串口输出流
     * @param freq 频点，用ASCII字符串表示，如:9800,即设置频点为98MHz（Tuner以10KHz为单位）
     * @throws NumberFormatException 当参数不符合上述规定时报错
     * @throws IOException 串口输出流报错
     */
    public synchronized void setFreq(OutputStream os,String freq) throws IOException,NumberFormatException{
        try {
            sendRequest(os, RequestType.SET_FREQ,freq);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询频点
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getFreq(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.CHECK_FREQ);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置接收模式
     * @param os 串口输出流
     * @param mode 用ASCII字符串表示：2：模式2  3：模式3
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
            sendRequest(os, RequestType.CHECK_REV_MODE);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置运行模式
     * @param os 串口输出流
     * @param mode 用ASCII字符串表示：
     *1：模式1：采用用户设置的固定的频点，使用此模式用户必须先设置频点
     *2：模式2：进行自动搜台模式，并将可用的频点信息写入到flash
     *3：模式3：采用flash中保存的频点，如果flash中没有保存频点数据，或者flash中的频点数据不能解数据，自动切换到模式2进行搜台
     * @throws IOException 串口输出流报错
     * @throws InputFormatException 当参数不符合上述规定时报错
     * @throws NumberFormatException 当参数不符合上述规定时报错
     */
    public synchronized void setRunningMode(OutputStream os,String mode) throws IOException, InputFormatException,NumberFormatException {
        sendRequest(os, RequestType.SET_RUN_MODE,mode);
    }

    /**
     * 查询当前运行模式
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getRunningMode(OutputStream os) throws IOException {
        try {
            sendRequest(os, RequestType.CHECK_RUN_MODE);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

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
            sendRequest(os, RequestType.CHECK_CKFO);
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
            sendRequest(os, RequestType.CHECK_1PPS);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动系统升级。发送启动升级命令后，需要手动点击复位按钮，程序重启后生效
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void beginSysUpgrade(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.SYS_UPGRADE_START);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询SK9042模块当前系统版本。
     * @param os 串口输出流
     * @throws IOException 串口输出流报错
     */
    public synchronized void getSysVersion(OutputStream os) throws IOException{
        try {
            sendRequest(os, RequestType.VERSION);
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
            sendRequest(os, RequestType.CHECK_CHIP_ID);
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
            sendRequest(os, RequestType.SNR);
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
            sendRequest(os, RequestType.SYS_STATE);
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
            sendRequest(os, RequestType.SFO);
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
            sendRequest(os, RequestType.CFO);
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
            sendRequest(os, RequestType.TUNER);
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
            sendRequest(os, RequestType.LDPC);
        } catch (InputFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log等级设置
     *  @param os 串口输出流
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
        sendRequest(os, RequestType.SET_LOG_LEVEL);
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
            case SYS_UPGRADE_START:
            case CHECK_FREQ:
            case CHECK_REV_MODE:
            case CHECK_RUN_MODE:
            case CHECK_CKFO:
            case CHECK_1PPS:
            case TIME:
            case VERSION:
            case CHECK_CHIP_ID:
            case SNR:
            case SYS_STATE:
            case SFO:
            case CFO:
            case TUNER:
            case LDPC:
                sb.append(rq.toString()).append(mAppendix);
                break;
            case SET_BD_RATE:
                Integer i3=Integer.valueOf((String)params[0]);
                if(i3==1||i3==2){
                    Integer i4=Integer.valueOf((String)params[1]);
                    if(i4==9600||i4==57600||i4==19200||i4==115200){
                        sb.append(rq.toString()).append("=").append(params[0]).append(",").append(params[2]).append(mAppendix);
                    }else {
                        throw new InputFormatException("Serial port baud rate is confined to 9600, 57600, 19200, 115200.");
                    }
                }else {
                    throw new InputFormatException("Serial port No. is confined to 1 or 2.");
                }
                break;
            case SET_FREQ:
                Integer.valueOf((String)params[0]);//先检查一下字符串是否能转成数字。
                sb= buildCmdWithParam0(rq,params[0]);
                break;
            case SET_REV_MODE:
                Integer i5=Integer.valueOf((String)params[0]);
                if(i5==2||i5==3){
                    sb= buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Receive mode is confined to 2 or 3.");
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
            case SYS_UPGRADE_UPLOAD_DATA:
                //// TODO: 2018/3/16 相关协议还没出来
                break;
            case SET_CHIP_ID:
                String t1= (String) params[0];
                if(t1.length()<=20){
                    sb=buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("The length of the ID must be no less than 20.");
                }
                break;
            case SET_RUN_MODE:
                Integer i1=Integer.valueOf((String)params[0]);
                if(i1>=1&&i1<=3){
                    sb=buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Running mode is confined only to 1, 2, or 3.");
                }
                break;
            case SET_LOG_LEVEL:
                Integer i2 = Integer.valueOf((String) params[0]);
                if(i2>=0&&i2<=5){
                    buildCmdWithParam0(rq,params[0]);
                }else {
                    throw new InputFormatException("Log level must be within the range of [0-5].");
                }
                break;
            default:
                break;
        }
        os.write(sb.toString().getBytes());
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

}
