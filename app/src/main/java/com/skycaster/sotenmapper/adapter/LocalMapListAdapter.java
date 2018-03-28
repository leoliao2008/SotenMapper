package com.skycaster.sotenmapper.adapter;

import android.content.Context;
import android.view.View;

import com.skycaster.sotenmapper.base.MyBaseAdapter;
import com.skycaster.sotenmapper.bean.MyMKOLUpdateElement;
import com.skycaster.sotenmapper.vh.LocalMapViewHolder;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class LocalMapListAdapter extends MyBaseAdapter<MyMKOLUpdateElement,LocalMapViewHolder> {
    private LocalMapListAdapter.CallBack mCallBack;

    public LocalMapListAdapter(ArrayList<MyMKOLUpdateElement> list, Context context, int layoutId,CallBack callBack) {
        super(list, context, layoutId);
        mCallBack=callBack;
    }

    @Override
    protected LocalMapViewHolder initViewHolder(View rootView) {
        LocalMapViewHolder vh = new LocalMapViewHolder(rootView);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        return getList().get(position).cityID;
    }

    @Override
    protected void initConvertView(final MyMKOLUpdateElement bean, LocalMapViewHolder vh, int position) {
        vh.getTv_city().setText(bean.cityName);
        vh.getProgressBar().setProgress(bean.ratio);
        vh.getTv_progress().setText(bean.ratio+"%");
        vh.getBtn_delete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onDelete(bean.cityID);
            }
        });
        if(bean.update){
            vh.getBtn_Update().setEnabled(true);
            vh.getBtn_Update().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onRequestUpdate(bean.cityID);
                }
            });
        }else {
            vh.getBtn_Update().setEnabled(false);
        }
    }

    public interface CallBack{
        void onDelete(int cityId);
        void onRequestUpdate(int cityId);
    }


}
