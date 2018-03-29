package com.skycaster.gps_decipher_lib;


import com.skycaster.gps_decipher_lib.GPGGA.GPGGABean;
import com.skycaster.gps_decipher_lib.GPGGA.TbGNGGABean;
import com.skycaster.gps_decipher_lib.GPGSA.GPGSABean;
import com.skycaster.gps_decipher_lib.GPGSV.GPGSVBean;

/**
 * Created by 廖华凯 on 2017/10/27.
 */

public class GPSDataExtractorCallBack {
    public void onGetGPGGABean(GPGGABean bean){}

    public void onGetTBGNGGABean(TbGNGGABean bean){}

    public void onGetGPGSVBean(GPGSVBean bean) {}

    public void onGetGPGSABean(GPGSABean bean) {

    }
}
