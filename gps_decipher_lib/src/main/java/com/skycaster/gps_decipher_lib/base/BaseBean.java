package com.skycaster.gps_decipher_lib.base;

import android.util.Log;

/**
 * Created by 廖华凯 on 2018/3/29.
 */

public abstract class BaseBean {
    private String mTag;

    public BaseBean() {
        mTag=getClass().getSimpleName();
    }

    protected void showLog(String msg){
        Log.e(mTag,msg);
    }

    public abstract String getRawString();
}
