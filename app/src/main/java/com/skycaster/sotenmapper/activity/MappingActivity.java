package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.map.MapView;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.MappingPresenter;

import butterknife.BindView;


public class MappingActivity extends BaseMVPActivity<MappingPresenter> {

    @BindView(R.id.map_view)
    MapView mMapView;

    public static void start(Context context) {
        Intent starter = new Intent(context, MappingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_mapping;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new MappingPresenter(this);

    }

    @Override
    protected void initListener() {

    }

    public MapView getMapView() {
        return mMapView;
    }
}
