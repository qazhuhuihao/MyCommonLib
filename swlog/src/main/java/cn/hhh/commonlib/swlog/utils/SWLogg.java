package cn.hhh.commonlib.swlog.utils;

import android.util.Log;

import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;
import cn.hhh.commonlib.utils.Logg;

/**
 * @author qazhu
 * @date 2017/12/11
 */

public class SWLogg implements Logg.LoggInterface {

    private final String TAG = this.getClass().getSimpleName();

    public static boolean printToSW = false;

    private static SWLogg swLogg;

    private SWLogg(){}

    public static SWLogg getInstance(){
        if (null == swLogg)
            swLogg = new SWLogg();

        return swLogg;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void v(String tag, String msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.VERBOSE, tag, msg));
    }

    @Override
    public void w(String tag, String msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.WARN, tag, msg));
    }

    @Override
    public void i(String tag, String msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.INFO, tag, msg));
    }

    @Override
    public void d(String tag, String msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.DEBUG, tag, msg));
    }

    @Override
    public void e(String tag, String msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, tag, msg));
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, tag, e));
    }

    @Override
    public void sysOut(Object msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.INFO, "", String.valueOf(msg)));
    }

    @Override
    public void sysErr(Object msg) {
        if (printToSW)
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.ERROR, "", String.valueOf(msg)));
    }
}
