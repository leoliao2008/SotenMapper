package com.skycaster.sotenmapper.presenter;

import android.support.v4.app.Fragment;

import com.skycaster.sotenmapper.activity.MapAdminActivity;
import com.skycaster.sotenmapper.adapter.MapAdminPagerAdapter;
import com.skycaster.sotenmapper.base.BasePresenter;
import com.skycaster.sotenmapper.fragment.AvailMapListFragment;
import com.skycaster.sotenmapper.fragment.LocalMapListFragment;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class MapAdminPresenter extends BasePresenter {
    private MapAdminActivity mActivity;

    public MapAdminPresenter(MapAdminActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate() {
        initViewPager();
    }

    private void initViewPager() {
        LocalMapListFragment f1=new LocalMapListFragment();
        AvailMapListFragment f2=new AvailMapListFragment();
        ArrayList<Fragment> list=new ArrayList<>();
        list.add(f1);
        list.add(f2);
        MapAdminPagerAdapter pagerAdapter=new MapAdminPagerAdapter(
                mActivity.getSupportFragmentManager(),
                list
        );
        mActivity.getViewPager().setAdapter(pagerAdapter);
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

    }
}
