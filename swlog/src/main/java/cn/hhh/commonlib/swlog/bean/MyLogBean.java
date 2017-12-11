package cn.hhh.commonlib.swlog.bean;

import java.util.Date;

/**
 *
 * @author qazhu
 * @date 2017/12/10
 */

public class MyLogBean {

    /**
     * 级别
     * {@link android.util.Log#ERROR}
     */
    public int logLevel;
    public Date date;
    public String logTag;
    public String logMsg;

    public MyLogBean(int logLevel, String logTag, String logMsg){
        this.logLevel = logLevel;
        this.date = new Date();
        this.logTag = logTag;
        this.logMsg = logMsg;
    }
}
