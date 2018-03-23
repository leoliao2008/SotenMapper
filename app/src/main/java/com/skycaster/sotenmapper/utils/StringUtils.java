package com.skycaster.sotenmapper.utils;

/**
 * Created by 廖华凯 on 2018/3/15.
 */

public class StringUtils {
    public static String hexToString(byte[] tempBytes) {
        StringBuilder sb=new StringBuilder();
        for(byte temp:tempBytes){
            sb.append("0x");
            sb.append(Integer.toHexString(temp));
            sb.append(" ");
        }
        return sb.toString();
    }
}
