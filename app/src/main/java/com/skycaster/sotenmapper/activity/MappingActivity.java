package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextSwitcher;

import com.baidu.mapapi.map.MapView;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.MappingPresenter;
import com.skycaster.sotenmapper.widget.LanternView;

import butterknife.BindView;


public class MappingActivity extends BaseMVPActivity<MappingPresenter> {

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.txt_switcher)
    TextSwitcher mTxtSwitcher;
    @BindView(R.id.lantern_view)
    LanternView mLanternView;

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

    public TextSwitcher getTxtSwitcher() {
        return mTxtSwitcher;
    }

    public LanternView getLanternView() {
        return mLanternView;
    }

    //不需要客户在这个页面设置参数，统一在事先在设置页面设置好了再进来。
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.mapping_activity_menu,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.set_freq:
                mPresenter.setCdRadioFreq();
                break;
            case R.id.check_freq:
                mPresenter.getCdRadioFreq();
                break;
            default:
                break;
        }
        return true;
    }

}
