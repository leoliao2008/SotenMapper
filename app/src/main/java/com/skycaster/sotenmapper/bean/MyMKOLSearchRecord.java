package com.skycaster.sotenmapper.bean;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class MyMKOLSearchRecord extends MKOLSearchRecord{
    public int cityID;
    /** @deprecated */
    public int size;
    public long dataSize;
    public String cityName="null";
    public int cityType;
    public ArrayList<MKOLSearchRecord> childCities;
    public boolean isDownLoaded;

    public MyMKOLSearchRecord() {
        super();
    }

    public MyMKOLSearchRecord(MKOLSearchRecord record){
        this.cityID=record.cityID;
        this.size=record.size;
        this.dataSize=record.dataSize;
        this.cityName=record.cityName;
        this.cityType=record.cityType;
        this.childCities=record.childCities;
    }



}
