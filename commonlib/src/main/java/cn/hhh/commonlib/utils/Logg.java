package cn.hhh.commonlib.utils;

import cn.hhh.commonlib.common.Configs;

/**
 * function : 日志输出.
 * <p></p>
 * Created by lzj on 2015/12/31.
 */
@SuppressWarnings("all")
public final class Logg {
    private static boolean DEBUG = Configs.DEBUG;

    public static void v(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.v(tag, msg);
    }

    public static void v(final String tag, Object... objs) {
        if (!DEBUG) return;
        android.util.Log.v(tag, getInfo(objs));
    }

    public static void w(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.w(tag, msg);
    }

    public static void w(final String tag, Object... objs) {
        if (!DEBUG) return;
        android.util.Log.w(tag, getInfo(objs));
    }

    public static void i(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.i(tag, msg);
    }

    public static void i(final String tag, Object... objs) {
        if (!DEBUG) return;
        android.util.Log.i(tag, getInfo(objs));
    }

    public static void d(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.d(tag, msg);
    }

    public static void d(final String tag, Object... objs) {
        if (!DEBUG) return;
        android.util.Log.d(tag, getInfo(objs));
    }

    public static void e(String tag, String msg) {
        if (!DEBUG) return;
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        if (!DEBUG) return;
        android.util.Log.e(tag, msg, e);
    }

    public static void e(final String tag, Object... objs) {
        if (!DEBUG) return;
        android.util.Log.e(tag, getInfo(objs));
    }

    private static String getInfo(Object... objs) {
        StringBuilder sb = new StringBuilder();
        if (null == objs) {
            sb.append("no message.");
        } else {
            for (Object object : objs) {
                sb.append("--");
                sb.append(object);
            }
        }
        return sb.toString();
    }

    public static void sysOut(Object msg) {
        if (!DEBUG) return;
        System.out.println(msg);
    }

    public static void sysErr(Object msg) {
        if (!DEBUG) return;
        System.err.println(msg);
    }
}
