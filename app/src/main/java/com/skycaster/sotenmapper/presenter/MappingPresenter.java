package com.skycaster.sotenmapper.presenter;

import com.baidu.mapapi.map.MapView;
import com.skycaster.sotenmapper.activity.MappingActivity;
import com.skycaster.sotenmapper.base.BasePresenter;

/**
 * Created by 廖华凯 on 2018/3/26.
 */

public class MappingPresenter extends BasePresenter {
    private MappingActivity mActivity;
    private MapView mMapView;

    public MappingPresenter(MappingActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        mMapView=mActivity.getMapView();

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }
}
