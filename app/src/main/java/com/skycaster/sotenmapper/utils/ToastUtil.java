package com.skycaster.sotenmapper.utils;

import android.widget.Toast;

import com.skycaster.sotenmapper.base.BaseApplication;

/**
 * Created by 廖华凯 on 2018/3/14.
 */

public class ToastUtil {
    private static volatile Toast TOAST;
    private ToastUtil(){}
    public synchronized static void showToast(String msg){
        if(TOAST==null){
            TOAST=Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_SHORT);
        }else {
            TOAST.setText(msg);
        }
        TOAST.show();
    }
}
