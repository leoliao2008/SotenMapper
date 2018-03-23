package com.skycaster.sk9042_lib.request;

/**
 * Created by 廖华凯 on 2018/3/15.
 * 一个枚举类，包含了SK9042的全部请求类型
 */

public enum RequestType {
    TEST_CONN,RESET,TIME,SET_BD_RATE,SET_FREQ,CHECK_FREQ,SET_REV_MODE,CHECK_REV_MODE,SET_RUN_MODE,CHECK_RUN_MODE,SYS_UPGRADE_START,
    SYS_UPGRADE_UPLOAD_DATA,TOGGLE_1PPS, CHECK_1PPS,TOGGLE_CKFO,CHECK_CKFO,VERSION, SET_CHIP_ID,CHECK_CHIP_ID,SNR,SYS_STATE,SFO,
    CFO,TUNER,LDPC,SET_LOG_LEVEL;

    /**
     * 改写了每个枚举成员对应的字符串，方便拼接成指令。
     * @return 用于拼接指令的字符串。
     */
    @Override
    public String toString() {
        switch (this){
            case TEST_CONN:
                return "AT";
            case RESET:
                return "AT+RESET";
            case TIME:
                return "AT+TIME?";
            case SET_BD_RATE:
                return "AT+BDRT";
            case SET_FREQ:
                return "AT+FREQ";
            case CHECK_FREQ:
                return "AT+FREQ?";
            case SET_REV_MODE:
                return "AT+RMODE";
            case CHECK_REV_MODE:
                return "AT+RMODE?";
            case SET_RUN_MODE:
                return "AT+RM";
            case CHECK_RUN_MODE:
                return "AT+RM?";
            case SYS_UPGRADE_START:
                return "AT+STUD";
            case SYS_UPGRADE_UPLOAD_DATA:
                return "AT+UDDA";
            case TOGGLE_1PPS:
                return "AT+1PPS";
            case CHECK_1PPS:
                return "AT+1PPS?";
            case TOGGLE_CKFO:
                return "AT+CKFO";
            case CHECK_CKFO:
                return "AT+CKFO?";
            case VERSION:
                return "AT+SVER?";
            case SET_CHIP_ID:
                return "AT+ID";
            case CHECK_CHIP_ID:
                return "AT+ID?";
            case SNR:
                return "AT+SNR?";
            case SYS_STATE:
                return "AT+STAT?";
            case SFO:
                return "AT+SFO?";
            case CFO:
                return "AT+CFO?";
            case TUNER:
                return "AT+TUNER?";
            case LDPC:
                return "AT+LDPC?";
            case SET_LOG_LEVEL:
                return "AT+LOG";
            default:
                return super.toString();
        }
    }
}
