package com.skycaster.sotenmapper.adapter;

import android.content.Context;
import android.view.View;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.MyBaseAdapter;
import com.skycaster.sotenmapper.bean.MyMKOLSearchRecord;
import com.skycaster.sotenmapper.vh.AvailableMapViewHolder;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class AvailMapListAdapter extends MyBaseAdapter<MyMKOLSearchRecord,AvailableMapViewHolder> {
    private AvailMapListAdapter.CallBack mCallBack;

    public AvailMapListAdapter(ArrayList<MyMKOLSearchRecord> list, Context context, int layoutId,CallBack callBack) {
        super(list, context, layoutId);
        mCallBack=callBack;
    }

    @Override
    public long getItemId(int position) {
        return getList().get(position).cityID;
    }

    @Override
    protected AvailableMapViewHolder initViewHolder(View rootView) {
        return new AvailableMapViewHolder(rootView);
    }

    @Override
    protected void initConvertView(final MyMKOLSearchRecord bean, AvailableMapViewHolder vh, int position) {
        if(vh==null){
            showLog("vh == null");
        }
        if(vh.getTv_city()==null){
            showLog("city == null");
        }

        if(bean==null){
            showLog("bean == null");
        }

        if(bean.cityName==null){
            showLog("city name ==null");
        }
        vh.getTv_city().setText(bean.cityName);
        vh.getBtn_downLoad().setText(bean.isDownLoaded?getContext().getString(R.string.downloaded):getContext().getString(R.string.begin_to_download));
        if(bean.isDownLoaded){
            vh.getBtn_downLoad().setEnabled(false);
            vh.getBtn_downLoad().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onRequestDownLoad(bean.cityID);
                }
            });
        }else {
            vh.getBtn_downLoad().setEnabled(true);
        }
    }

    public interface CallBack{
        void onRequestDownLoad(int cityId);
    }
}
