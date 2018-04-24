package com.skycaster.sotenmapper.module;

import android.content.Context;
import android.os.Environment;

import com.soten.libs.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2018/4/17.
 * 一个保存字符串到本地目录的类
 */

public class FileWriterModule {

    private FileInputStream mInputStream;//保留
    private FileOutputStream mOutputStream;
    private AtomicBoolean mIsPause=new AtomicBoolean(false);

    /**
     * 生成本地文件，用来保存数据。
     * @param context 背景
     * @param fileName 本地文件的名字
     * @throws IOException 创建文件失败后会报这个异常，一般是由于App本地读取权限忘记申请所致。
     */
    public FileWriterModule(Context context, String fileName) throws IOException {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + context.getPackageName();//目标文件目录
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();//首次创建，生成新目录
        }
        File file=new File(dir,fileName+".txt");//定义文档
        if(file.exists()){
            file.delete();//如果已经存在该文档，则删除
        }
        if(file.createNewFile()){
            //生成文档获取流
            mInputStream=new FileInputStream(file);
            mOutputStream =new FileOutputStream(file);
        }
    }

    /**
     * 把数据保存到生成的本地文件中去
     * @param data 数据，以二进制表示
     * @param len 有效长度
     * @throws IOException 文件读写异常
     */
    public synchronized void write(byte[] data,int len) throws IOException {
        if(mOutputStream !=null&&!mIsPause.get()){
            mOutputStream.write(data,0,len);
            mOutputStream.write("\r\n".getBytes());//因为原始定位数据没有自带换行符，为了阅读方便，所以要增加换行符
            mOutputStream.flush();
        }
    }

    /**
     * 暂停保存数据
     */
    public synchronized void pause(){
        mIsPause.set(true);
    }

    /**
     * 继续保存数据
     */
    public synchronized void resume(){
        mIsPause.set(false);
    }

    /**
     * 结束记录后关闭流以节省系统资源。
     * @throws IOException 文件读写异常
     */
    public synchronized void close() throws IOException {
        mOutputStream.flush();
        mOutputStream.close();
    }

    /**
     * 根据当前日期生产一个唯一的文件名
     * @return 文件名
     */
    public static String genFileNameByDate(){
        Date date=new Date();
        String patter="MM月dd日HH时mm分ss秒";
        SimpleDateFormat format=new SimpleDateFormat(patter, Locale.CHINA);
        return format.format(date);
    }

    private void showLog(String msg){
        LogUtils.e(getClass().getSimpleName(),msg);
    }


}
