package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.skycaster.sotenmapper.R;

public class SettingActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_menu);
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
