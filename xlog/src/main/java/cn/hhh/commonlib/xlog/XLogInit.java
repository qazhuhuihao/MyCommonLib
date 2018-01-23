package cn.hhh.commonlib.xlog;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.hhh.commonlib.utils.FileStorageUtil;
import cn.hhh.commonlib.utils.Logg;
import me.pqpo.librarylog4a.Level;
import me.pqpo.librarylog4a.Log4a;
import me.pqpo.librarylog4a.Logger;
import me.pqpo.librarylog4a.appender.FileAppender;
import me.pqpo.librarylog4a.formatter.DateFileFormatter;

/**
 * @author qazhu
 * @date 2018/1/23
 */

public class XLogInit {

    @SuppressWarnings("WeakerAccess")
    public static final int BUFFER_SIZE = 1024 * 400; //400k

    public static void init(Context context) {
        int level = Level.DEBUG;

        File log = FileStorageUtil.getLogDir();
        String buffer_path = log.getAbsolutePath() + File.separator + ".logCache";
        String time = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date());
        String log_path = log.getAbsolutePath() + File.separator + "Log_" + time + ".log";
        FileAppender.Builder fileBuild = new FileAppender.Builder(context)
                .setLogFilePath(log_path)
                .setLevel(level)
                .setBufferFilePath(buffer_path)
                .setFormatter(new DateFileFormatter())
                .setBufferSize(BUFFER_SIZE);
        Logger logger = new Logger.Builder()
                //.enableAndroidAppender(androidBuild)
                .enableFileAppender(fileBuild)
                .create();
        Log4a.setLogger(logger);

        Logg.addLogg(XLog.getInstance());
    }

    public static void flush() {
        Log4a.flush();
    }

    @SuppressWarnings("unused")
    public static void release() {
        Log4a.release();
    }

    private static class XLog implements Logg.LoggInterface {

        private final String TAG = this.getClass().getSimpleName();

        private static XLog xLog;

        private XLog() {
        }

        static XLog getInstance() {
            if (null == xLog)
                xLog = new XLog();

            return xLog;
        }

        @Override
        public String getTAG() {
            return TAG;
        }

        @Override
        public void v(String tag, String msg) {
            Log4a.v(tag, msg);
        }

        @Override
        public void w(String tag, String msg) {
            Log4a.w(tag, msg);
        }

        @Override
        public void i(String tag, String msg) {
            Log4a.i(tag, msg);
        }

        @Override
        public void d(String tag, String msg) {
            Log4a.d(tag, msg);
        }

        @Override
        public void e(String tag, String msg) {
            Log4a.e(tag, msg);
        }

        @Override
        public void e(String tag, String msg, Throwable e) {
            Log4a.e(tag, msg, e);
        }

        @Override
        public void sysOut(Object msg) {
            Log4a.i("", String.valueOf(msg));
        }

        @Override
        public void sysErr(Object msg) {
            Log4a.e("", String.valueOf(msg));
        }
    }
}
