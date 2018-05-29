package com.skycaster.gps_decipher_lib.GPGSV;

import android.text.TextUtils;

import com.skycaster.gps_decipher_lib.GPSDataExtractorCallBack;
import com.skycaster.gps_decipher_lib.base.BaseBean;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by 廖华凯 on 2018/1/8.
 * GPS Satellites in View（GSV）可见卫星信息，包含了当前卫星个数、PRN码、方位、俯仰角等，可以用来绘制卫星图
 * 参考网站：http://blog.csdn.net/lzyzuixin/article/details/6161507
 */
public class GPGSVBean extends BaseBean {
//    例：$GPGSV,3,1,10,20,78,331,45,01,59,235,47,22,41,069,,13,32,252,45*70
//    字段0：$GPGSV，语句ID，表明该语句为GPS Satellites in View（GSV）可见卫星信息
//    字段1：本次GSV语句的总数目（1 - 3）
//    字段2：本条GSV语句是本次GSV语句的第几条（1 - 3）
//    字段3：当前可见卫星总数（00 - 12）（前导位数不足则补0）
//    字段4：PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
//    字段5：卫星仰角（00 - 90）度（前导位数不足则补0）
//    字段6：卫星方位角（00 - 359）度（前导位数不足则补0）
//    字段7：信噪比（00－99）dbHz
//    字段8：PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
//    字段9：卫星仰角（00 - 90）度（前导位数不足则补0）
//    字段10：卫星方位角（00 - 359）度（前导位数不足则补0）
//    字段11：信噪比（00－99）dbHz
//    字段12：PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
//    字段13：卫星仰角（00 - 90）度（前导位数不足则补0）
//    字段14：卫星方位角（00 - 359）度（前导位数不足则补0）
//    字段15：信噪比（00－99）dbHz
//    字段16：PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
//    字段17：卫星仰角（00 - 90）度（前导位数不足则补0）
//    字段18：卫星方位角（00 - 359）度（前导位数不足则补0）
//    字段19：信噪比（00－99）dbHz
//    字段20：校验值
    private ArrayList<PRNBean> mPrns=new ArrayList<>();
    private int mPrnIndex;//mPrns的指针，因为卫星数据可能分几块接收，因此要记录上一次PRN的指针位置，下次接着这个位置记录
    private int mCurrentBlock=-1;
    private int mTotalBlock=-1;
    private int mExpectBlock=-1;
    private int mSatCount=-1;

    private static final GPGSVBean singleton=new GPGSVBean();
    private GPGSVBean (){}

    public synchronized static GPGSVBean getInstance(){
        return singleton;
    }


    public synchronized void decipher(String src, GPSDataExtractorCallBack callBack){

        int endIndex = src.indexOf('*');
        if(endIndex<0){
            return;
        }
        //把校验码甩掉
        src=src.substring(0,endIndex);
        String[] splits = src.split(Pattern.quote(","));
        //如果不符合标准格式，则跳过后面的代码
//        if(splits.length!=20){//根据实际情况发现不一定20个，可能少于这个数。
//            return;
//        }

        //字段1：本次GSV语句的总数目（1 - 3）
        int totalBlock= TextUtils.isEmpty(splits[1])?1: Integer.valueOf(splits[1]);
        //字段2：本条GSV语句是本次GSV语句的第几条（1 - 3）
        int currentBlock= TextUtils.isEmpty(splits[2])?1: Integer.valueOf(splits[2]);
        //字段3：当前可见卫星总数（00 - 12）（前导位数不足则补0）
        int satCount= TextUtils.isEmpty(splits[3])?0: Integer.valueOf(splits[3]);

        if(mCurrentBlock==-1&&mTotalBlock==-1&&mSatCount==-1){
            //GPGSV数据是分好几批发过来的，如果当前是卫星定位的首批数据，直接写入
            updateGPGSVBean(splits,callBack);
        }else {
            //如果不是首批数据，判断一下是否和前面衔接得上：
            if(totalBlock==mTotalBlock&&currentBlock==mExpectBlock&&satCount==mSatCount){
                //如果衔接得上，直接更新
                updateGPGSVBean(splits, callBack);
            }else {
                //如果衔接不上，说明数据丢失了，如果当前是新的卫星数据的首批数据块，则重新填充
                if(currentBlock==1){
                    reset();
                    updateGPGSVBean(splits, callBack);
                }
            }

        }
    }

    private synchronized void updateGPGSVBean(String[] splits, GPSDataExtractorCallBack callBack){
        mTotalBlock= TextUtils.isEmpty(splits[1])?1: Integer.valueOf(splits[1]);
        mCurrentBlock= TextUtils.isEmpty(splits[2])?1: Integer.valueOf(splits[2]);
        mSatCount= TextUtils.isEmpty(splits[3])?0: Integer.valueOf(splits[3]);
        //fill prns
        for(int i=1;i<5&&(mPrnIndex<mSatCount);i++){//mPrnIndex<mSatCount ：PRN数目和卫星数目是一样的。
            int index=i*4;
            if(index>=splits.length){//防止下标越位
                break;
            }
            PRNBean prn=new PRNBean();
            //字段4：PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
            //字段5：卫星仰角（00 - 90）度（前导位数不足则补0）
            //字段6：卫星方位角（00 - 359）度（前导位数不足则补0）
            //字段7：信噪比（00－99）dbHz
            prn.setPrnCode(TextUtils.isEmpty(splits[index])?"00":splits[index]);
            prn.setElevation(TextUtils.isEmpty(splits[index+1])?0: Float.valueOf(splits[index+1]));
            prn.setAzimuth(TextUtils.isEmpty(splits[index+2])?0: Float.valueOf(splits[index+2]));
            if(index+3<splits.length){//根据硬件不同，有时会报ArrayIndexOutOfBoundsException错误，特此修正
                prn.setSnr(TextUtils.isEmpty(splits[index+3])?0: Float.valueOf(splits[index+3]));
            }else {
                prn.setSnr(0);
            }
            mPrns.add(mPrnIndex++,prn);
        }

        if(mTotalBlock!=mCurrentBlock){
            //如果当前不是最后一批数据，则接着读取下一批
            mExpectBlock=mCurrentBlock+1;
        }else {
            //如果当前是最后一批数据，则发送解析后的GPGSV数据给下层调用
            callBack.onGetGPGSVBean(this.copy());
            //重置参数
            reset();
        }

    }

    /**
     * 深拷贝
     * @return
     */
    private GPGSVBean copy() {
        GPGSVBean copy=new GPGSVBean();
        copy.mSatCount=this.mSatCount;
        for(int i=0;i<mPrns.size();i++){
            copy.mPrns.add(this.mPrns.get(i));
        }
        return copy;
    }

    /**
     * 获取所有的伪随机噪音码
     * @return
     */
    public ArrayList<PRNBean> getPrns() {
        return mPrns;
    }

    /**
     * 获取当前卫星总数
     * @return
     */
    public int getSatCount() {
        return mSatCount;
    }


    /**
     * 清空，还原到默认状态
     */
    private synchronized void reset() {
        mCurrentBlock=-1;
        mTotalBlock=-1;
        mExpectBlock=-1;
        mSatCount=-1;
        mPrnIndex=0;
        mPrns.clear();
    }

    @Override
    public String toString() {
        return "GPGSVBean{" +
                "mPrns=" + mPrns +
                ", mTotalBlock=" + mTotalBlock +
                ", mSatCount=" + mSatCount +
                '}';
    }

    /**
     * 输出GPGSV的裸数据，如 $GPGSV,3,1,10,20,78,331,45,01,59,235,47,22,41,069,,13,32,252,45*70
     * @return
     */
    @Override
    public String getRawString() {
        return toString();
    }
}
