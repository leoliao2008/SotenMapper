package com.skycaster.sotenmapper.module;


import java.io.File;
import java.io.IOException;

import project.SerialPort.SerialPort;
import project.SerialPort.SerialPortFinder;


/**
 * Created by 廖华凯 on 2018/3/12.
 */

public class SerialPortModule {

    public String[] getAvailablePaths(){
        return new SerialPortFinder().getAllDevicesPath();
    }

    public SerialPort openSerialPort(String path, int bdRate) throws SecurityException,IOException {
        return new SerialPort(new File(path),bdRate,0);
    }

    public void close(SerialPort sp){
        if(sp==null){
            return;
        }
        sp.close();
    }
}
