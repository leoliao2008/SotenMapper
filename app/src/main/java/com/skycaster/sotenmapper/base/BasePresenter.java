package com.skycaster.sotenmapper.base;

import android.util.Log;

import com.skycaster.sotenmapper.impl.ImplBasePresenter;
import com.skycaster.sotenmapper.utils.ToastUtil;

/**
 * Created by 廖华凯 on 2018/3/15.
 */

public abstract class BasePresenter implements ImplBasePresenter {
    private String mTag;
    protected BasePresenter(){
        mTag=getClass().getSimpleName();
    }

    protected void showLog(String msg){
        Log.e(mTag,msg);
    }

    public void handleException(Exception e){
        ToastUtil.showToast(e.getMessage());
    }
}
