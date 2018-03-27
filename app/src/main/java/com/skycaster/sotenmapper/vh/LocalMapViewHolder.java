package com.skycaster.sotenmapper.vh;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.MyBaseViewHolder;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class LocalMapViewHolder extends MyBaseViewHolder {

    private TextView mTv_city;
    private TextView mTv_progress;
    private ProgressBar mProgressBar;
    private Button mBtn_Update;
    private Button mBtn_delete;


    public LocalMapViewHolder(View convertView) {
        super(convertView);
    }

    @Override
    protected void iniViews(View convertView) {
        mTv_city = (TextView) findViewById(R.id.tv_city);
        mTv_progress = (TextView) findViewById(R.id.tv_download_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mBtn_Update = (Button) findViewById(R.id.btn_update);
        mBtn_delete = (Button) findViewById(R.id.btn_delete);
    }

    public TextView getTv_city() {
        return mTv_city;
    }

    public TextView getTv_progress() {
        return mTv_progress;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public Button getBtn_Update() {
        return mBtn_Update;
    }

    public Button getBtn_delete() {
        return mBtn_delete;
    }


}
