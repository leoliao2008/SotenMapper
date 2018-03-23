package com.skycaster.sk9042_lib.ack;

/**
 * Created by 廖华凯 on 2018/3/16.
 * 发送指令给SK9042后，监听返回结果的回调。
 */

public abstract class RequestCallBack {
    /**
     * 返回SK9042当前是否已经连接上，只有连接成功了才会有回调。
     * @param isConnected true 连接成功
     */
    public void testConnection(boolean isConnected){}

    /**
     * SK9042复位请求的回调
     * @param isSuccess 复位成功/失败
     */
    public void reset(boolean isSuccess){}

    /**
     * 获取系统时间的回调
     * @param time 用ASCII字符串表示；如:2017,03,05,14,10,05表示2017年3月5日14点10分5秒
     */
    public void getSysTime(String time){}

    /**
     * 设置波特率的回调
     * @param isSuccess 设置成功/失败
     */
    public void setBaudRate(boolean isSuccess) {

    }

    public void setFreq(boolean isSuccess) {
        
    }

    /**
     * 获取频点的回调
     * @param isAvailable  true 表示有数据，false表示FLASH没有写入频点信息
     * @param freq 一个可以转成int类型的字符串，如:“9800”,即频点为98MHz（Tuner以10KHz为单位）
     */
    public void getFreq(boolean isAvailable, String freq) {

    }

    public void setReceiveMode(boolean isSuccess) {

    }

    /**
     * 获取接收模式的回调
     * @param mode 用ASCII字符串表示：2：模式2 3：模式3
     */
    public void getReceiveMode(String mode) {

    }

    public void setRunningMode(boolean isSuccess) {

    }

    /**
     *
     * @param mode 用ASCII字符串表示：
     *1：模式1：采用用户设置的固定的频点，使用此模式用户必须先设置频点
     *2：模式2：进行自动搜台模式，并将可用的频点信息写入到flash
     *3：模式3：采用flash中保存的频点，如果flash中没有保存频点数据，或者flash中的频点数据不能解数据，自动切换到模式2进行搜台
     */
    public void getRunningMode(String mode) {

    }

    public void toggleCKFO(boolean isSuccess) {

    }

    /**
     *
     * @param isOpen 用ASCII字符串表示；ENABLE：使能。校验失败不输出。DISABLE：禁止。不校验输出。
     */
    public void checkIsOpenCKFO(String isOpen) {

    }

    public void toggle1PPS(boolean isSuccess) {

    }

    /**
     *
     * @param isOpen 用ASCII字符串表示；OPEN： 1PPS开启 CLOSE： 1PPS关闭
     */
    public void checkIsOpen1PPS(String isOpen) {

    }

    /**
     *
     * @param version 用ASCII字符串表示，如:ver1.0
     */
    public void getSysVersion(String version) {

    }

    public void setChipId(boolean isSuccess) {

    }

    /**
     *
     * @param id 用ASCII字符串表示，最大长度不超过20。如:123
     */
    public void getChipId(String id) {

    }

    /**
     *
     * @param snr 用ASCII字符串表示，如:27.10
     */
    public void getSNR(String snr){}

    /**
     *
     * @param state 用ASCII字符串表示：
     * 0：未开机
     * 1：就绪
     * 2：锁定
     * 3：停止工作
     */
    public void getSysState(String state) {

    }

    /**
     *
     * @param sfo 用ASCII字符串表示，如:27.10
     */
    public void getSFO(String sfo) {

    }

    /**
     *
     * @param cfo 用ASCII字符串表示，如:27.10
     */
    public void getCFO(String cfo) {

    }


    /**
     *
     * @param isSet Tuner设置状态。用ASCII字符串表示：0：设置失败 1：设置成功
     * @param hasData Tuner数据状态。用ASCII字符串表示：0：无数据输入 1：有数据输入
     */
    public void getTunerState(String isSet, String hasData) {

    }

    /**
     *
     * @param passCnt 表示译码成功次数，无符号32位。用ASCII字符串表示，如:10,表示译码成功次数为10次。
     * @param failCnt 表示译码失败次数，无符号32位。用ASCII字符串表示，如:10,表示译码失败次数为10次。
     */
    public void getLDPC(String passCnt, String failCnt) {

    }
}
