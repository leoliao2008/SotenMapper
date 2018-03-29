package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.SatellitePresenter;
import com.skycaster.sotenmapper.widget.SatelliteMapView;

import butterknife.BindView;

public class SatelliteActivity extends BaseMVPActivity<SatellitePresenter> {


    @BindView(R.id.satellite_view)
    SatelliteMapView mSatelliteView;
    @BindView(R.id.tv_fix_status)
    TextView mTvFixStatus;
    @BindView(R.id.tv_sat_count)
    TextView mTvSatCount;
    @BindView(R.id.tv_lat)
    TextView mTvLat;
    @BindView(R.id.tv_lng)
    TextView mTvLng;
    @BindView(R.id.tv_alt)
    TextView mTvAlt;


    public static void start(Context context) {
        Intent starter = new Intent(context, SatelliteActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_satellite;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new SatellitePresenter(this);
    }

    @Override
    protected void initListener() {

    }

    public SatelliteMapView getSatelliteView() {
        return mSatelliteView;
    }

    public TextView getTvFixStatus() {
        return mTvFixStatus;
    }

    public TextView getTvSatCount() {
        return mTvSatCount;
    }

    public TextView getTvLat() {
        return mTvLat;
    }

    public TextView getTvLng() {
        return mTvLng;
    }

    public TextView getTvAlt() {
        return mTvAlt;
    }
}
