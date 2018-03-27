package com.skycaster.sotenmapper.base;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public abstract class MyBaseAdapter<ItemBean,VH extends MyBaseViewHolder> extends BaseAdapter{
    private ArrayList<ItemBean> mList;
    private Context mContext;
    private int mLayoutId;
    private String mTag;

    public MyBaseAdapter(ArrayList<ItemBean> list, Context context, int layoutId) {
        mList = list;
        mContext = context;
        mLayoutId=layoutId;
        mTag=getClass().getSimpleName();
    }

    public Context getContext() {
        return mContext;
    }

    protected ArrayList<ItemBean> getList(){
        return mList;
    }

    protected abstract VH initViewHolder(View rootView);

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if(convertView==null){
            convertView=View.inflate(mContext,mLayoutId,null);
            vh=initViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (VH) convertView.getTag();
        }
        initConvertView(mList.get(position),vh,position);
        return convertView;
    }

    protected abstract void initConvertView(ItemBean itemBean, VH vh, int position);

    protected void showLog(String msg){
        Log.e(mTag,msg);
    }
}
