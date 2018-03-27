package com.skycaster.sotenmapper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class MapAdminPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments;
    private String[] titles=new String[]{"本地离线地图","离线地图下载"};

    public MapAdminPagerAdapter(FragmentManager fm,ArrayList<Fragment>fragments) {
        super(fm);
        mFragments=fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
