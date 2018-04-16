package com.skycaster.sk9042_lib.ack;

import android.util.Log;

/**
 * Created by 廖华凯 on 2018/3/16.
 * 发送指令给SK9042后，监听返回结果的回调。
 */

public abstract class RequestCallBack {
    /**
     * 返回SK9042当前是否已经连接上，只有连接成功了才会有回调。
     * @param isConnected true 连接成功
     */
    protected void testConnection(boolean isConnected){}

    /**
     * SK9042复位请求的回调
     * @param isSuccess 复位成功/失败
     */
    protected void reset(boolean isSuccess){}

    /**
     * 获取系统时间的回调
     * @param time 用ASCII字符串表示；如:2017,03,05,14,10,05表示2017年3月5日14点10分5秒
     */
    protected void getSysTime(String time){}

    /**
     * 设置波特率的回调
     * @param isSuccess 设置成功/失败
     */
    protected void setBaudRate(boolean isSuccess) {

    }

    protected void setFreq(boolean isSuccess) {
        
    }

    /**
     * 获取频点的回调
     * @param isAvailable  true 表示有数据，false表示FLASH没有写入频点信息
     * @param freq 一个可以转成int类型的字符串，如:“9800”,即频点为98MHz（Tuner以10KHz为单位）
     */
    protected void getFreq(boolean isAvailable, String freq) {

    }

    protected void setReceiveMode(boolean isSuccess) {

    }

    /**
     * 获取接收模式的回调
     * @param mode 用ASCII字符串表示：2：模式2 3：模式3
     */
    protected void getReceiveMode(String mode) {

    }

    //此功能在1.4.2中被删除
//    public void setRunningMode(boolean isSuccess) {
//
//    }

//    /**
//     *
//     * @param mode 用ASCII字符串表示：
//     *1：模式1：采用用户设置的固定的频点，使用此模式用户必须先设置频点
//     *2：模式2：进行自动搜台模式，并将可用的频点信息写入到flash
//     *3：模式3：采用flash中保存的频点，如果flash中没有保存频点数据，或者flash中的频点数据不能解数据，自动切换到模式2进行搜台
//     */
//    public void getRunningMode(String mode) {
//
//    }

    protected void toggleCKFO(boolean isSuccess) {

    }

    /**
     *
     * @param isOpen 用ASCII字符串表示；ENABLE：使能。校验失败不输出。DISABLE：禁止。不校验输出。
     */
    protected void checkIsOpenCKFO(String isOpen) {

    }

    protected void toggle1PPS(boolean isSuccess) {

    }

    /**
     *
     * @param isOpen 用ASCII字符串表示；OPEN： 1PPS开启 CLOSE： 1PPS关闭
     */
    protected void checkIsOpen1PPS(String isOpen) {

    }

    /**
     *
     * @param version 用ASCII字符串表示，如:ver1.0
     */
    protected void getSysVersion(String version) {

    }

    protected void setChipId(boolean isSuccess) {

    }

    /**
     *
     * @param id 用ASCII字符串表示，最大长度不超过20。如:123
     */
    protected void getChipId(String id) {

    }

    /**
     *
     * @param snr 用ASCII字符串表示，如:27.10
     */
    protected void getSNR(String snr){}

    /**
     *
     * @param state 用ASCII字符串表示：
     * 0：未开机
     * 1：就绪
     * 2：锁定
     * 3：停止工作
     */
    protected void getSysState(String state) {

    }

    /**
     *
     * @param sfo 用ASCII字符串表示，如:27.10
     */
    protected void getSFO(String sfo) {

    }

    /**
     *
     * @param cfo 用ASCII字符串表示，如:27.10
     */
    protected void getCFO(String cfo) {

    }


    /**
     *
     * @param isSet Tuner设置状态。用ASCII字符串表示：0：设置失败 1：设置成功
     * @param hasData Tuner数据状态。用ASCII字符串表示：0：无数据输入 1：有数据输入
     */
    protected void getTunerState(String isSet, String hasData) {

    }

    /**
     *
     * @param passCnt 表示译码成功次数，无符号32位。用ASCII字符串表示，如:10,表示译码成功次数为10次。
     * @param failCnt 表示译码失败次数，无符号32位。用ASCII字符串表示，如:10,表示译码失败次数为10次。
     */
    protected void getLDPC(String passCnt, String failCnt) {

    }

    protected void setLogLevel(boolean isSuccess){

    }

    /**
     * 停止搜台的回调
     */
    protected void stopSearchFreq(){}

    /**
     * 开始搜台的回调
     * @param isFound 是否找到了合适的频点
     * @param result 搜索结果，以字符串表示，如“9800”
     */
    protected void startSearchFreq(boolean isFound, String result) {

    }

    /**
     * 判断特定频点下SK9042是否有信号
     * @param hasSignal
     */
    protected void verifyFreq(boolean hasSignal) {

    }

    /**
     * 发送升级指令后，sk9042返回的表示指令参数无误，接下来将开始复位的回调，
     * 在本回调中，客户端一般不用进行任何操作。
     */
    protected void onConfirmUpgradeStart() {

    }



    /**
     * 开始发送升级文件的回调
     */
    protected void onStartTransferringUpgradeFile() {

    }

    /**
     * 升级文件发送完毕的回调
     */
    protected void onFinishTransferringUpgradeFile() {

    }


    /**
     * sk9042系统升级结果的回调
     * @param isSuccess 是否升级成功
     * @param errorCode 错误码，只有在升级失败的情况下才有意义，否则为-1。
     *1：表示升级超时
     *2：表示bin文件校验失败
     *注：无论出现何种错误，固件都会还原到之前的版本
     */
    protected void onUpgradeFinish(boolean isSuccess, String errorCode) {

    }

    /**
     * 获取SK9042差分数据输出端串口的波特率
     * @param isValid 波特率是否为有效数字，当为true的时候，可以把result转成整数，false的时候，result表示实际获取的结果，以字符串表示。
     * @param result 查询结果
     */
    public void getBaudRate(boolean isValid, String result) {

    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
