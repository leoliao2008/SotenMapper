package com.skycaster.sotenmapper.bean;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by 廖华凯 on 2018/3/28.
 */

public class MyMKOLUpdateElement extends MKOLUpdateElement {
    public int cityID;
    public String cityName;
    public int ratio;
    public int status;
    public LatLng geoPt;
    public int size;
    public int serversize;
    public int level;
    public boolean update;

    public MyMKOLUpdateElement(MKOLUpdateElement element) {
        cityID=element.cityID;
        cityName=element.cityName;
        ratio=element.ratio;
        status=element.status;
        geoPt=element.geoPt;
        size=element.size;
        serversize=element.serversize;
        level=element.level;
        update=element.update;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MKOLUpdateElement){
            return cityID==((MKOLUpdateElement) obj).cityID;
        }
        return super.equals(obj);
    }


}
