package cn.hhh.commonlib.swlog.bean;

import java.util.Date;

import cn.hhh.commonlib.utils.DateTimeUtil;

/**
 * @author qazhu
 * @date 2017/12/10
 */

@SuppressWarnings("WeakerAccess")
public class MyLogBean {

    /**
     * 级别
     * {@link android.util.Log#ERROR}
     */
    public int logLevel;
    public Date date;
    public String logTag;
    public String logMsg;

    public MyLogBean(int logLevel, String logTag, String logMsg) {
        this.logLevel = logLevel;
        this.date = new Date();
        this.logTag = logTag;
        this.logMsg = logMsg;
    }

    public MyLogBean(int logLevel, String logTag, Throwable e) {
        this(logLevel, logTag, null, e);
    }

    public MyLogBean(int logLevel, String logTag, String msg, Throwable e) {
        this.logLevel = logLevel;
        this.date = new Date();
        this.logTag = logTag;

        int n = 9 + logTag.length();

        StringBuilder spaceStringBuilder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            spaceStringBuilder.append(" ");
        }

        String space = spaceStringBuilder.toString();

        StackTraceElement[] stackTraceElements = e.getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();

        if (null == msg) {
            stringBuilder.append(e.toString()).append("\n");
        } else {
            stringBuilder.append(msg).append("\n");
            stringBuilder.append(space).append(e.toString()).append("\n");
        }
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append(space).append(stackTraceElement.toString()).append("\n");
        }

        this.logMsg = stringBuilder.toString();
    }

    public static void add(StringBuilder sb, MyLogBean bean) {
        sb.append(DateTimeUtil.formatDate(bean.date, DateTimeUtil.DF_HH_MM_SS)).append(" ").append(bean.logTag).append(" ").append(bean.logMsg).append("\n");
    }
}
