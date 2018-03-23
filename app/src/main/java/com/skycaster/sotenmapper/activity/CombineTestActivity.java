package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.presenter.CombineTestActivityPresenter;

import butterknife.BindView;


public class CombineTestActivity extends BaseMVPActivity<CombineTestActivityPresenter> {

    @BindView(R.id.act_test_tgbtn_cd_radio_pw)
    ToggleButton tgbtn_cdRadioPw;
    @BindView(R.id.act_test_tgbtn_gps_pw)
    ToggleButton tgbtn_gpsPw;
    @BindView(R.id.act_test_btn_cd_radio_sp_setting)
    Button btn_cdRadioSpSetting;
    @BindView(R.id.act_test_lstv_cd_radio_func_menu)
    ListView lstv_cdRadioFuncMenu;
    @BindView(R.id.act_test_lstv_feed_back_console)
    ListView lstv_feedBackConsole;

    public static void start(Context context) {
        Intent starter = new Intent(context, CombineTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.combine_activity_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new CombineTestActivityPresenter(this);

    }

    @Override
    protected void initListener() {
        btn_cdRadioSpSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showCdRadioSpConfigWindow();
            }
        });

        tgbtn_cdRadioPw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.toggleCdRadioPw(isChecked);
            }
        });

        tgbtn_gpsPw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.toggleGpsPw(isChecked);
            }
        });

    }


    public ListView getLstv_cdRadioFuncMenu() {
        return lstv_cdRadioFuncMenu;
    }

    public ListView getLstv_feedBackConsole() {
        return lstv_feedBackConsole;
    }
}
