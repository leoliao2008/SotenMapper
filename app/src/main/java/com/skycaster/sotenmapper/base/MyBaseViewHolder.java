package com.skycaster.sotenmapper.base;

import android.util.Log;
import android.view.View;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public abstract class MyBaseViewHolder {
    private View mConvertView;
    private String mTag;

    private MyBaseViewHolder(){
        mTag=getClass().getSimpleName();
    }

    public MyBaseViewHolder(View convertView){
        this();
        mConvertView=convertView;
        iniViews(convertView);
    }

    public View getConvertView(){return mConvertView;}

    protected abstract void iniViews(View convertView);

    protected View findViewById(int id){
        return mConvertView.findViewById(id);
    }

    protected void showLog(String msg){
        Log.e(mTag,msg);
    }
}
