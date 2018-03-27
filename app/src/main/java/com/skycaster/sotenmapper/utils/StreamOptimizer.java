package com.skycaster.sotenmapper.utils;

/**
 * Created by 廖华凯 on 2018/3/26.
 */

public class StreamOptimizer {

    private int mReadPipeFd;
    private int mWritePipeFd;
    private int mEpollFd;

    static {
        System.loadLibrary("stream_optimizer-lib");
    }

    public native int pollInner(int timeoutMillis);

    public native boolean open();

    public native boolean addFd(int fd, int events);

    public native boolean removeFd(int fd);

    public native void close();

    public native void wake();
}
