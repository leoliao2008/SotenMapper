package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.MapAdminPresenter;

import butterknife.BindView;

public class MapAdminActivity extends BaseMVPActivity<MapAdminPresenter> {

    @BindView(R.id.pager_title_strip)
    PagerTitleStrip mPagerTitleStrip;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private MKOfflineMap mMapManager;

    public static void start(Context context) {
        Intent starter = new Intent(context, MapAdminActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_map_admin;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new MapAdminPresenter(this);
    }

    @Override
    protected void initListener() {

    }

    public PagerTitleStrip getPagerTitleStrip() {
        return mPagerTitleStrip;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public MKOfflineMap getMapManager() {
        return mMapManager;
    }

    public void setMapManager(MKOfflineMap mapManager) {
        mMapManager = mapManager;
    }
}
