package com.skycaster.sotenmapper.module;

import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PowerManager;

import com.soten.libs.utils.PowerManagerUtils;

/**
 * Created by 廖华凯 on 2018/3/12.
 * 前期测试用控制各个模块电源的类，目前此类的功能已经整合到各个模块中去了，很少情况能用得上了。
 */

public class PowCtrlModule {
    private PowerManager mPowerManager;

    public PowCtrlModule(PowerManager powerManager) {
        mPowerManager = powerManager;
    }

    public void cdRadioPower(boolean isToPwUp){
        int deviceCode=0x02;
        if(isToPwUp){
            openDevice(deviceCode);
        }else {
            closeDevice(deviceCode);
        }
    }

    public void pdnPower(boolean isToPwUp){
        int deviceCode=0x0C;
        if(isToPwUp){
            openDevice(deviceCode);
        }else {
            closeDevice(deviceCode);
        }
    }

    public void gpsPower(LocationManager manager,boolean isToPwUp,LocationListener locationListener,GpsStatus.NmeaListener nmeaListener) throws SecurityException {
        if(isToPwUp){
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0.01f,
                    locationListener
            );
            manager.addNmeaListener(nmeaListener);
        }else {
            manager.removeUpdates(locationListener);
            manager.removeNmeaListener(nmeaListener);
        }

    }

    public boolean isGpsRunning(LocationManager manager){
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void openDevice(int deviceCode) {
        PowerManagerUtils.open(mPowerManager,deviceCode);
    }

    private void closeDevice(int deviceCode){
        PowerManagerUtils.close(mPowerManager,deviceCode);
    }
}
