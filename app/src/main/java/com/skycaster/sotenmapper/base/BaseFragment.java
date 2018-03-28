package com.skycaster.sotenmapper.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by 廖华凯 on 2018/3/28.
 */

public class BaseFragment extends Fragment{
    private String mTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag=getClass().getSimpleName();
    }

    protected void showLog(String msg){
        Log.e(mTag,msg);
    }
}
