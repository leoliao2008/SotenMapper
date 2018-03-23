package com.skycaster.sk9042_lib.request;

/**
 * Created by 廖华凯 on 2018/3/15.
 * 当向SK9042模块发送指令时，如果参数不符合协议规范，这个类就会提示开发者相关信息。
 */
public class InputFormatException extends Exception {
    public InputFormatException(String s) {
        super(s);
    }
}
