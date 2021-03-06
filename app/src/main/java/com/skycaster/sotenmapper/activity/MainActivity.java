package com.skycaster.sotenmapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skycaster.sotenmapper.R;

public class MainActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void toSettingActivity(View view) {
        SettingActivity.start(this);
    }

    public void toMappingActivity(View view) {
        MappingActivity.start(this);
    }

    public void toSatelliteActivity(View view) {
        SatelliteActivity.start(this);
    }
}
