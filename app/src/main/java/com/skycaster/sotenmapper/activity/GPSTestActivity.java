package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.GPSTestPresenter;

import butterknife.BindView;

public class GPSTestActivity extends BaseMVPActivity<GPSTestPresenter> {


    @BindView(R.id.tgbtn_sk9042)
    ToggleButton mTgbtnSk9042;
    @BindView(R.id.tgbtn_gps)
    ToggleButton mTgbtnGps;
//    @BindView(R.id.btn_sp_setting)
//    Button mBtnSpSetting;
    @BindView(R.id.list_view)
    ListView mListView;


    public static void start(Context context) {
        Intent starter = new Intent(context, GPSTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_gpsmodule_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new GPSTestPresenter(this);

    }

    @Override
    protected void initListener() {
        mTgbtnSk9042.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.ctrlSk9042Power(isChecked);
            }
        });

        mTgbtnGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.ctrlGpsPower(isChecked);
            }
        });

//        mBtnSpSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.serialPortSetting();
//            }
//        });

    }



    public ListView getListView() {
        return mListView;
    }


}
