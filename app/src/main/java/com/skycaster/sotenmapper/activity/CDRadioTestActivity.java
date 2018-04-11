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
import com.skycaster.sotenmapper.presenter.CDRadioTestPresenter;

import butterknife.BindView;

/**
 * Created by 廖华凯 on 2018/3/22.
 *
 */

public class CDRadioTestActivity extends BaseMVPActivity<CDRadioTestPresenter> {
    @BindView(R.id.tgbtn_pow_ctrl)
    ToggleButton mTgbtnPowCtrl;
    @BindView(R.id.btn_sp_setting)
    Button mBtnSpSetting;
    @BindView(R.id.list_view_test_menu)
    ListView mListViewTestMenu;
    @BindView(R.id.list_view_console)
    ListView mListViewConsole;

    public static void start(Context context) {
        Intent starter = new Intent(context, CDRadioTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setContentLayout() {
        return R.layout.activity_cd_radio_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void instantiatePresenter() {
        mPresenter = new CDRadioTestPresenter(this);
    }

    @Override
    protected void initListener() {
        mTgbtnPowCtrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.toggleCdRadioPw(isChecked);
            }
        });

        mBtnSpSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showSpSettingWindow();
            }
        });

    }

    public ListView getListViewTestMenu() {
        return mListViewTestMenu;
    }

    public ListView getListViewConsole() {
        return mListViewConsole;
    }
}
