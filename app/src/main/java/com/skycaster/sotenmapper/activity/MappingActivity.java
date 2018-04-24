package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
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
    private AtomicBoolean isPauseRecordLoc=new AtomicBoolean(false);//是否已经暂停记录定位信息
    private ActionMode mActionModeRecordLocData;


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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.toggle_gpgga_recorder://切换actionbar的内容，开始记录定位信息到本地
                mPresenter.startRecordingGpggaData();
                mActionModeRecordLocData = startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        getMenuInflater().inflate(R.menu.action_mode_record_gpgga_data, menu);
                        mode.setTitle("定位记录中");
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        //暂停和继续只能同时显示一个
                        MenuItem itemPause = menu.findItem(R.id.pause);
                        MenuItem itemPlay = menu.findItem(R.id.play);
                        itemPause.setVisible(!isPauseRecordLoc.get());
                        itemPlay.setVisible(isPauseRecordLoc.get());
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.play://继续
                                mPresenter.resumeRecordingCpggaData();
                                isPauseRecordLoc.set(false);
                                mode.invalidate();
                                break;
                            case R.id.pause://暂停
                                mPresenter.pauseRecordingGpggaData();
                                isPauseRecordLoc.set(true);
                                mode.invalidate();
                                break;
                            case R.id.save://保存并退出模式
                                mActionModeRecordLocData.finish();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        //退出模式时，终止记录定位
                        try {
                            mPresenter.terminateRecordingGpggaData();
                        } catch (IOException e) {
                            mPresenter.handleException(e);
                        }

                    }
                });
                break;
            default:
                break;
        }
        return true;
    }


}
