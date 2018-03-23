package com.skycaster.sotenmapper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.skycaster.sotenmapper.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testGPS(View view) {
        GPSTestActivity.start(this);
    }

    public void testCDRadio(View view) {
        CDRadioTestActivity.start(this);
    }

    public void combineTest(View view) {
        CombineTestActivity.start(this);
    }
}
