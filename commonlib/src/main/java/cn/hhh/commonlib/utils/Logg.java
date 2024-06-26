package cn.hhh.commonlib.utils;

import android.util.Log;

import androidx.collection.ArraySet;

import java.util.Set;

import cn.hhh.commonlib.common.Configs;

/**
 * function : 日志输出.
 * <p></p>
 * Created by lzj on 2015/12/31.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Logg {
    private static boolean DEBUG = Configs.DEBUG;

    private static Set<LoggInterface> loggs = new ArraySet<>();

    public static Set<LoggInterface> getLoggs() {
        return loggs;
    }

    public static void addLogg(LoggInterface logg) {
        loggs.add(logg);
    }

    public static void v(String tag, String msg) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.v(tag, msg);
        }
    }

    public static void v(final String tag, Object... objs) {
        v(tag, getInfo(objs));
    }

    public static void w(String tag, String msg) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.w(tag, msg);
        }
    }

    public static void w(final String tag, Object... objs) {
        w(tag, getInfo(objs));
    }

    public static void i(String tag, String msg) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.i(tag, msg);
        }
    }

    public static void i(final String tag, Object... objs) {
        i(tag, getInfo(objs));
    }

    public static void d(String tag, String msg) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.d(tag, msg);
        }
    }

    public static void d(final String tag, Object... objs) {
        d(tag, getInfo(objs));

    }

    public static void e(String tag, String msg) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        msg = filterNull(msg);
        for (LoggInterface logg : loggs) {
            logg.e(tag, msg, e);
        }
    }

    public static void e(final String tag, Object... objs) {
        e(tag, getInfo(objs));

    }

    public static void sysOut(Object msg) {
        for (LoggInterface logg : loggs) {
            logg.sysOut(msg);
        }
    }

    public static void sysErr(Object msg) {
        for (LoggInterface logg : loggs) {
            logg.sysErr(msg);
        }
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

    private static String filterNull(String msg) {
        return msg == null ? "null" : msg;
    }

    public interface LoggInterface {
        String getTAG();

        void v(String tag, String msg);

        void w(String tag, String msg);

        void i(String tag, String msg);

        void d(String tag, String msg);

        void e(String tag, String msg);

        void e(String tag, String msg, Throwable e);

        void sysOut(Object msg);

        void sysErr(Object msg);
    }

    public static class LoggExample implements LoggInterface {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public String getTAG() {
            return TAG;
        }

        @Override
        public void v(String tag, String msg) {
            if (!DEBUG) return;
            logLongMessage(Log.VERBOSE, tag, msg);
        }

        @Override
        public void w(String tag, String msg) {
            if (!DEBUG) return;
            logLongMessage(Log.WARN, tag, msg);
        }

        @Override
        public void i(String tag, String msg) {
            if (!DEBUG) return;
            logLongMessage(Log.INFO, tag, msg);
        }

        @Override
        public void d(String tag, String msg) {
            if (!DEBUG) return;
            logLongMessage(Log.DEBUG, tag, msg);
        }

        @Override
        public void e(String tag, String msg) {
            if (!DEBUG) return;
            logLongMessage(Log.ERROR, tag, msg);
        }

        @Override
        public void e(String tag, String msg, Throwable e) {
            if (!DEBUG) return;
            Log.e(tag, msg, e);
        }

        @Override
        public void sysOut(Object msg) {
            if (!DEBUG) return;
            System.out.println(msg);
        }

        @Override
        public void sysErr(Object msg) {
            if (!DEBUG) return;
            System.err.println(msg);
        }

        private void logLongMessage(int level, String tag, String longMsg) {
            int maxLogSize = 3200;
            for (int i = 0; i < longMsg.length(); i += maxLogSize) {
                int end = Math.min(i + maxLogSize, longMsg.length());
                switch (level) {
                    case Log.VERBOSE:
                        Log.v(tag, longMsg.substring(i, end));
                        break;
                    case Log.WARN:
                        Log.w(tag, longMsg.substring(i, end));
                        break;
                    case Log.INFO:
                        Log.i(tag, longMsg.substring(i, end));
                        break;
                    case Log.DEBUG:
                        Log.d(tag, longMsg.substring(i, end));
                        break;
                    case Log.ERROR:
                        Log.e(tag, longMsg.substring(i, end));
                        break;
                    case Log.ASSERT:
                        Log.wtf(tag, longMsg.substring(i, end));
                        break;
                    default:
                        // Handle other levels if needed
                        break;
                }
            }
        }
    }
}
