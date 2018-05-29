package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.tv_version)
    TextView mTvVersion;

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_menu);
        ButterKnife.bind(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            mTvVersion.setText("App版本：Ver."+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void toOffMapAdmin(View view) {
        MapAdminActivity.start(this);
    }

    public void toGPSTest(View view) {
        GPSTestActivity.start(this);
    }

    public void toSk9042Test(View view) {
        CDRadioTestActivity.start(this);
    }
}
