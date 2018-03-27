package com.skycaster.sotenmapper.vh;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.MyBaseViewHolder;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class AvailableMapViewHolder extends MyBaseViewHolder {

    private TextView mTv_city;
    private Button mBtn_downLoad;

    public AvailableMapViewHolder(View convertView) {
        super(convertView);
    }

    @Override
    protected void iniViews(View convertView) {
        mTv_city = convertView.findViewById(R.id.tv_city);
        mBtn_downLoad = convertView.findViewById(R.id.btn_download);
    }

    public TextView getTv_city() {
        return mTv_city;
    }

    public Button getBtn_downLoad() {
        return mBtn_downLoad;
    }
}
