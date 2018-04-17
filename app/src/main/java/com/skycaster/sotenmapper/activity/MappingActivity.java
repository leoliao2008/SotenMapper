package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextSwitcher;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.MapView;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.MappingPresenter;
import com.skycaster.sotenmapper.widget.LanternView;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;


public class MappingActivity extends BaseMVPActivity<MappingPresenter> {

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.txt_switcher)
    TextSwitcher mTxtSwitcher;
    @BindView(R.id.lantern_view)
    LanternView mLanternView;
    @BindView(R.id.simulation_test)
    ToggleButton mSimulationTest;
    private AtomicBoolean isRecordGpggaData=new AtomicBoolean(false);

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
        mSimulationTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mPresenter.startTest();
                }else {
                    mPresenter.stopTest();
                }
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapping_activity_menu,menu);
        MenuItem item = menu.findItem(R.id.toggle_gpgga_recorder);
        if(isRecordGpggaData.get()){
            item.setTitle(getResources().getString(R.string.stop_record_gpgga_data));
        }else {
            item.setTitle(getResources().getString(R.string.start_record_gpgga_data));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.toggle_gpgga_recorder:
                isRecordGpggaData.set(!isRecordGpggaData.get());
                if(isRecordGpggaData.get()){
                    mPresenter.startRecordingGpggaData();
                }else {
                    try {
                        mPresenter.stopRecordingGpggaData();
                    } catch (IOException e) {
                        mPresenter.handleException(e);
                    }
                }
                invalidateOptionsMenu();
                break;
            default:
                break;
        }
        return true;
    }


}
