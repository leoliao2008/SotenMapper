package com.skycaster.sotenmapper.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.skycaster.sotenmapper.impl.ImplBasePresenter;
import com.skycaster.sotenmapper.utils.ToastUtil;

import butterknife.ButterKnife;

/**
 * Created by 廖华凯 on 2018/3/12.
 * MVP模式中的base activity，注意一定要在instantiatePresenter()中初始化presenter
 */

public abstract class BaseMVPActivity<T extends ImplBasePresenter> extends AppCompatActivity {
    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentLayout());
        ButterKnife.bind(this);
        initView();
        instantiatePresenter();
        mPresenter.onCreate();//相当于initData()，这部分功能交由presenter来处理比较好
        initListener();
    }

    protected abstract int setContentLayout();

    protected abstract void initView();

    /**
     * 必须在这里初始化presenter，否则会报空指针。
     */
    protected abstract void instantiatePresenter();

    protected abstract void initListener();

    protected T getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    protected void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    protected void showToast(String msg){
        ToastUtil.showToast(msg);
    }
}
