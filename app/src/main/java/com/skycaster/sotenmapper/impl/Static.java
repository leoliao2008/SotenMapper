package com.skycaster.sotenmapper.impl;

/**
 * Created by 廖华凯 on 2018/3/12.
 */

public interface Static {
    String SHARED_PREFERENCES_NAME="MY_SHARED_PREFERENCES";
    String CD_RADIO_BD_RATES ="CD_RADIO_BD_RATES";
    String CD_RADIO_SP_PATH ="CD_RADIO_SP_PATH";
    String GPS_BD_RATE="GPS_BD_RATE";
    String GPS_SP_PATH="GPS_SP_PATH";
    String DEFAULT_GPS_SP_PATH="/dev/ttyMT1";
    int DEFAULT_GPS_SP_BD_RATE=9600;
    String DEFAULT_CD_RADIO_SP_PATH="/dev/ttyMT2";
    int DEFAULT_CD_RADIO_SP_BD_RATE=57600;
    String BAIDU_LBS_APP_KEY="vQokgZ4ly1giDVjpPdiPw1qA2GoeMGTc";

    String LAST_UPDATE = "LAST_UPDATE";
}
