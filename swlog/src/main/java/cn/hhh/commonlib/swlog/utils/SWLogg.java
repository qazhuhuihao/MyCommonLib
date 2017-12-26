package cn.hhh.commonlib.swlog.utils;

import android.util.Log;

import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;
import cn.hhh.commonlib.utils.Logg;

/**
 * @author qazhu
 * @date 2017/12/11
 */

public class SWLogg extends Logg.LoggExample {

    public static boolean printToSW = false;

    //private static final String PRINT_TO_SW = "printToSW";

//    public static void setPrintToSW(boolean isOpen){
//        SPManager.saveBoolean(PRINT_TO_SW,isOpen);
//        if ()
//    }

    @Override
    public void v(String tag, String msg) {
        super.v(tag, msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.VERBOSE, tag, msg));
    }

    @Override
    public void w(String tag, String msg) {
        super.w(tag, msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.WARN, tag, msg));
    }

    @Override
    public void i(String tag, String msg) {
        super.i(tag, msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.INFO, tag, msg));
    }

    @Override
    public void d(String tag, String msg) {
        super.d(tag, msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.DEBUG, tag, msg));
    }

    @Override
    public void e(String tag, String msg) {
        super.e(tag, msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, tag, msg));
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        super.e(tag, msg, e);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, tag, e));
    }

    @Override
    public void sysOut(Object msg) {
        super.sysOut(msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.INFO, "", String.valueOf(msg)));
    }

    @Override
    public void sysErr(Object msg) {
        super.sysErr(msg);
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, "", String.valueOf(msg)));
    }
}
