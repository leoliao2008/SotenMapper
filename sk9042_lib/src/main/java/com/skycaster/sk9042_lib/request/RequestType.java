package com.skycaster.sk9042_lib.request;

/**
 * Created by 廖华凯 on 2018/3/15.
 * 一个枚举类，包含了SK9042的全部请求类型
 */

public enum RequestType {
    TEST_CONN,RESET, GET_TIME,SET_BD_RATE,SET_FREQ, GET_FREQ,SET_REV_MODE, GET_REV_MODE,SYS_UPGRADE_START,
    TOGGLE_1PPS, GET_1PPS,TOGGLE_CKFO, GET_CKFO, GET_VERSION, SET_CHIP_ID, GET_CHIP_ID, GET_SNR, GET_SYS_STATE, GET_SFO,
    GET_CFO, GET_TUNER, GET_LDPC,SET_LOG_LEVEL, AUTO_MATCH_FREQ,VERIFY_FREQ,GET_BD_RATE;

    /**
     * 改写了每个枚举成员对应的字符串，方便拼接成指令。
     * @return 用于拼接指令的字符串。
     */
    @Override
    public String toString() {
        switch (this){
            case TEST_CONN://测试连接
                return "AT";
            case RESET://算法复位
                return "AT+RESET";
            case GET_TIME://系统时间
                return "AT+TIME?";
            case SET_BD_RATE://设置波特率
                return "AT+BDRT";
            case SET_FREQ://设置频点
                return "AT+FREQ";
            case GET_FREQ://查询频点
                return "AT+FREQ?";
            case SET_REV_MODE://设置接收模式
                return "AT+RMODE";
            case GET_REV_MODE://查询接收模式
                return "AT+RMODE?";
            case SYS_UPGRADE_START://启动升级
                return "AT+STUD:";
            case TOGGLE_1PPS://切换1pps开关
                return "AT+1PPS";
            case GET_1PPS://查询1pps开关情况
                return "AT+1PPS?";
            case TOGGLE_CKFO://设置数据输出校验功能
                return "AT+CKFO";
            case GET_CKFO://查询数据输出校验功能
                return "AT+CKFO?";
            case GET_VERSION://查询SK9042软件版本
                return "AT+SVER?";
            case SET_CHIP_ID://查询芯片ID
                return "AT+ID";
            case GET_CHIP_ID://获取芯片ID
                return "AT+ID?";
            case GET_SNR://查询音噪率
                return "AT+SNR?";
            case GET_SYS_STATE://查询系统状态
                return "AT+STAT?";
            case GET_SFO://查询时偏
                return "AT+SFO?";
            case GET_CFO://查询频偏
                return "AT+CFO?";
            case GET_TUNER://查询tuner状态
                return "AT+TUNER?";
            case GET_LDPC://LDPC译码统计
                return "AT+LDPC?";
            case SET_LOG_LEVEL://等级设置
                return "AT+LOG";
            case AUTO_MATCH_FREQ://自动搜频
                return "AT+SEARCH";
            case VERIFY_FREQ://查询该频点下是否有数据输出
                return "AT+CHECK:";
            case GET_BD_RATE://查询波特率
                return "AT+BDRT?";
            default:
                return super.toString();
        }
    }
}
