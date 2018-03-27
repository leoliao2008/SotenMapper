package com.skycaster.sotenmapper.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.baidu.mapapi.SDKInitializer;
import com.skycaster.sotenmapper.impl.Static;

/**
 * Created by 廖华凯 on 2018/3/12.
 */

public class BaseApplication extends Application {
    private static SharedPreferences SP;
    private static Handler HANDLER;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        SP=getSharedPreferences(Static.SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        HANDLER=new Handler();
        mContext=getApplicationContext();
        //初始化百度地图
        SDKInitializer.initialize(mContext);
    }

    public static SharedPreferences getSharedPreferences() {
        return SP;
    }

    public static void post(Runnable runnable){
        HANDLER.post(runnable);
    }

    public static void removeCallbacks(Runnable runnable){
        HANDLER.removeCallbacks(runnable);
    }

    public static Context getContext() {
        return mContext;
    }
}
