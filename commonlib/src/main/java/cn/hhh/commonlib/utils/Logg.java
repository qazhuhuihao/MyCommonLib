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

    private static LoggInterface logg = new LoggExample();

    public static String getTAG(){
        return logg.getTAG();
    }

    public static void setLogg(LoggInterface logg) {
        Logg.logg = logg;
    }

    public static void v(String tag, String msg) {
        logg.v(tag, msg);
    }

    public static void v(final String tag, Object... objs) {
        logg.v(tag, objs);
    }

    public static void w(String tag, String msg) {
        logg.w(tag, msg);
    }

    public static void w(final String tag, Object... objs) {
        logg.w(tag, objs);
    }

    public static void i(String tag, String msg) {
        logg.i(tag, msg);
    }

    public static void i(final String tag, Object... objs) {
        logg.i(tag, objs);
    }

    public static void d(String tag, String msg) {
        logg.d(tag, msg);
    }

    public static void d(final String tag, Object... objs) {
        logg.d(tag, objs);
    }

    public static void e(String tag, String msg) {
        logg.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        logg.e(tag, msg, e);
    }

    public static void e(final String tag, Object... objs) {
        logg.e(tag, objs);
    }

    public static void sysOut(Object msg) {
        logg.sysOut(msg);
    }

    public static void sysErr(Object msg) {
        logg.sysErr(msg);
    }

    public interface LoggInterface {
        String getTAG();

        void v(String tag, String msg);

        void v(final String tag, Object... objs);

        void w(String tag, String msg);

        void w(final String tag, Object... objs);

        void i(String tag, String msg);

        void i(final String tag, Object... objs);

        void d(String tag, String msg);

        void d(final String tag, Object... objs);

        void e(String tag, String msg);

        void e(String tag, String msg, Throwable e);

        void e(final String tag, Object... objs);

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
            android.util.Log.v(tag, msg);
        }

        @Override
        public void v(final String tag, Object... objs) {
            if (!DEBUG) return;
            android.util.Log.v(tag, getInfo(objs));
        }

        @Override
        public void w(String tag, String msg) {
            if (!DEBUG) return;
            android.util.Log.w(tag, msg);
        }

        @Override
        public void w(final String tag, Object... objs) {
            if (!DEBUG) return;
            android.util.Log.w(tag, getInfo(objs));
        }

        @Override
        public void i(String tag, String msg) {
            if (!DEBUG) return;
            android.util.Log.i(tag, msg);
        }

        @Override
        public void i(final String tag, Object... objs) {
            if (!DEBUG) return;
            android.util.Log.i(tag, getInfo(objs));
        }

        @Override
        public void d(String tag, String msg) {
            if (!DEBUG) return;
            android.util.Log.d(tag, msg);
        }

        @Override
        public void d(final String tag, Object... objs) {
            if (!DEBUG) return;
            android.util.Log.d(tag, getInfo(objs));
        }

        @Override
        public void e(String tag, String msg) {
            if (!DEBUG) return;
            android.util.Log.e(tag, msg);
        }

        @Override
        public void e(String tag, String msg, Throwable e) {
            if (!DEBUG) return;
            android.util.Log.e(tag, msg, e);
        }

        @Override
        public void e(final String tag, Object... objs) {
            if (!DEBUG) return;
            android.util.Log.e(tag, getInfo(objs));
        }

        private String getInfo(Object... objs) {
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
    }
}
