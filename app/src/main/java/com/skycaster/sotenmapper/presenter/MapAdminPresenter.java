package com.skycaster.sotenmapper.presenter;

import android.support.v4.view.PagerTitleStrip;
import android.util.TypedValue;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.MapAdminActivity;
import com.skycaster.sotenmapper.adapter.MapAdminPagerAdapter;
import com.skycaster.sotenmapper.base.BasePresenter;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class MapAdminPresenter extends BasePresenter {

    private MapAdminActivity mActivity;
    private MapAdminPagerAdapter mPagerAdapter;

    public MapAdminPresenter(MapAdminActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        mActivity.setMapManager(new MKOfflineMap());
        mActivity.getMapManager().init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int type, int state) {
                mPagerAdapter.getLocalMapListFragment().onGetOfflineMapState(type,state);
            }
        });
        initViewPager();

    }

    private void initViewPager() {
        mPagerAdapter = new MapAdminPagerAdapter(
                mActivity.getSupportFragmentManager(),
                mActivity.getMapManager()
        );
        mActivity.getViewPager().setAdapter(mPagerAdapter);
        PagerTitleStrip titleStrip = mActivity.getPagerTitleStrip();
        titleStrip.setBackgroundColor(mActivity.getResources().getColor(R.color.textGrey));
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP,mActivity.getResources().getDimension(R.dimen.sp_20));
        titleStrip.setTextSpacing(mActivity.getResources().getDimensionPixelSize(R.dimen.dp_5));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mActivity.getMapManager().destroy();

    }
}
