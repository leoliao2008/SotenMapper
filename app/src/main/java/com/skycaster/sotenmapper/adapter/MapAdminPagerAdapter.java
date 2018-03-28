package com.skycaster.sotenmapper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.skycaster.sotenmapper.fragment.AvailMapListFragment;
import com.skycaster.sotenmapper.fragment.LocalMapListFragment;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class MapAdminPagerAdapter extends FragmentPagerAdapter {
    private String[] titles=new String[]{"本地离线地图","离线地图下载"};
    private MKOfflineMap mMKOfflineMap;
    private LocalMapListFragment mLocalMapListFragment;
    private AvailMapListFragment mAvailMapListFragment;

    public MapAdminPagerAdapter(FragmentManager fm,MKOfflineMap mkOfflineMap) {
        super(fm);
        mMKOfflineMap=mkOfflineMap;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                mLocalMapListFragment=new LocalMapListFragment(mMKOfflineMap);
                return mLocalMapListFragment;
            case 1:
                mAvailMapListFragment=new AvailMapListFragment(mMKOfflineMap);
                return mAvailMapListFragment;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public LocalMapListFragment getLocalMapListFragment() {
        return mLocalMapListFragment;
    }

    public AvailMapListFragment getAvailMapListFragment() {
        return mAvailMapListFragment;
    }
}
