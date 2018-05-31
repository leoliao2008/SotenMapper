package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.base.BaseApplication;
import com.skycaster.sotenmapper.base.BaseMVPActivity;
import com.skycaster.sotenmapper.impl.Static;
import com.skycaster.sotenmapper.presenter.CDRadioTestPresenter;

import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean mIsShowRawStreamData=new AtomicBoolean(false);

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
        mIsShowRawStreamData.set(BaseApplication.getSharedPreferences().getBoolean(Static.KEY_IS_SHOW_RAW_STREAM,false));
    }

    @Override
    protected void initListener() {
        mTgbtnPowCtrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.toggleCdRadioPw(isChecked);
            }
        });
        //4月16日 把串口写死，页面启动后自动连接
//        mBtnSpSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.showSpSettingWindow();
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sp9042_setting,menu);
        MenuItem item = menu.findItem(R.id.menu_is_show_raw_stream);
        if(mIsShowRawStreamData.get()){
            item.setTitle("不要显示SK9042串口裸数据");
        }else {
            item.setTitle("显示SK9042串口裸数据");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_is_show_raw_stream:
                mIsShowRawStreamData.set(!mIsShowRawStreamData.get());
                BaseApplication.getSharedPreferences().edit().putBoolean(Static.KEY_IS_SHOW_RAW_STREAM,mIsShowRawStreamData.get()).apply();
                invalidateOptionsMenu();
                break;
            default:
                break;
        }
        return true;
    }

    public boolean isShowSK9042RawStreamData(){
        return mIsShowRawStreamData.get();
    }

    public ListView getListViewTestMenu() {
        return mListViewTestMenu;
    }

    public ListView getListViewConsole() {
        return mListViewConsole;
    }
}
