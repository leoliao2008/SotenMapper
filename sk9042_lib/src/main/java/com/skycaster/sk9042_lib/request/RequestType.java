package com.skycaster.sk9042_lib.request;

/**
 * Created by 廖华凯 on 2018/3/15.
 * 一个枚举类，包含了SK9042的全部请求类型
 */

public enum RequestType {
    TEST_CONN,RESET, GET_TIME,SET_BD_RATE,SET_FREQ, GET_FREQ,SET_REV_MODE, GET_REV_MODE,SYS_UPGRADE_START,
    SYS_UPGRADE_UPLOAD_DATA,TOGGLE_1PPS, GET_1PPS,TOGGLE_CKFO, GET_CKFO, GET_VERSION, SET_CHIP_ID, GET_CHIP_ID, GET_SNR, GET_SYS_STATE, GET_SFO,
    GET_CFO, GET_TUNER, GET_LDPC,SET_LOG_LEVEL, AUTO_MATCH_FREQ,VERIFY_FREQ;

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
            case GET_TIME:
                return "AT+TIME?";
            case SET_BD_RATE:
                return "AT+BDRT";
            case SET_FREQ:
                return "AT+FREQ";
            case GET_FREQ:
                return "AT+FREQ?";
            case SET_REV_MODE:
                return "AT+RMODE";
            case GET_REV_MODE:
                return "AT+RMODE?";
//            case SET_RUN_MODE:
//                return "AT+RM";
//            case GET_RUN_MODE:
//                return "AT+RM?";
            case SYS_UPGRADE_START:
                return "AT+STUD:";
            case SYS_UPGRADE_UPLOAD_DATA:
                return "AT+UDDA";
            case TOGGLE_1PPS:
                return "AT+1PPS";
            case GET_1PPS:
                return "AT+1PPS?";
            case TOGGLE_CKFO:
                return "AT+CKFO";
            case GET_CKFO:
                return "AT+CKFO?";
            case GET_VERSION:
                return "AT+SVER?";
            case SET_CHIP_ID:
                return "AT+ID";
            case GET_CHIP_ID:
                return "AT+ID?";
            case GET_SNR:
                return "AT+SNR?";
            case GET_SYS_STATE:
                return "AT+STAT?";
            case GET_SFO:
                return "AT+SFO?";
            case GET_CFO:
                return "AT+CFO?";
            case GET_TUNER:
                return "AT+TUNER?";
            case GET_LDPC:
                return "AT+LDPC?";
            case SET_LOG_LEVEL:
                return "AT+LOG";
            case AUTO_MATCH_FREQ:
                return "AT+SEARCH";
            case VERIFY_FREQ:
                return "AT+CHECK:";
            default:
                return super.toString();
        }
    }
}
