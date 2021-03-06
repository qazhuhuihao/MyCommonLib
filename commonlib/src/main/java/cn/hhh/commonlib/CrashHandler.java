package cn.hhh.commonlib;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.imp.FileNameSelector;
import cn.hhh.commonlib.utils.DateTimeUtil;
import cn.hhh.commonlib.utils.FileStorageUtil;
import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.PackageManagerUtil;
import cn.hhh.commonlib.utils.UIUtil;


/**
 * function: 截获（记录）崩溃. : 当程序产生未捕获异常则有此类接管并将异常记录在SD卡应用根目录或应用缓存目录的.crashLog文件夹下面.
 * <p></p>
 * Created by lzj on 2015/12/31.
 */
@SuppressWarnings("all")
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * 记录标志.
     */
    private static final String TAG = CrashHandler.class.getSimpleName();
    /**
     * CrashHandler实例.
     */
    private static final AtomicReference<CrashHandler> instance = new AtomicReference<>();

    /**
     * 初始化.
     */
    public static void init(Context context) {
        for (; ; ) {
            CrashHandler netManager = instance.get();
            if (netManager != null) return;
            netManager = new CrashHandler(context);
            if (instance.compareAndSet(null, netManager)) return;
        }
    }

    /**
     * 程序的Context对象.
     */
    private final Context mContext;
    /**
     * 用于格式化日期,作为日志文件名的一部分.
     */
    private final DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * 进程名字. 默认主进程名是包名
     */
    private final String mProcessName;
    /**
     * 系统默认的UncaughtException处理类.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final UncaughtExceptionHandler mDefaultHandler;

    /**
     * 保证只有一个CrashHandler实例.
     */
    private CrashHandler(Context context) {
        mContext = context.getApplicationContext();
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mProcessName = PackageManagerUtil.getProcessNameByPid(mContext, Process.myPid());
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        UIUtil.getHandler().removeCallbacksAndMessages(null);
        Logg.e(TAG, "---------------uncaughtException start---------------\r\n");
        Logg.e(TAG, "process [" + mProcessName + "],is abnormal!\r\n");
        Logg.e(TAG, "", throwable);
        try {
            handleException(thread, "", throwable);
        } catch (Exception ex) {
            Logg.e(TAG, "uncaughtException,ex:", ex);
        }
        Logg.e(TAG, "---------------uncaughtException end---------------\r\n");
        if (PackageManagerUtil.isMainProcess(mContext, mProcessName)) {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            if (intent != null) {
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (mgr != null && CommonBaseActivity.activityCount > 0)
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 300, restartIntent); // 300毫秒钟后重启应用activit
            }
        }
        Process.killProcess(Process.myPid());
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    @SuppressLint("DefaultLocale")
    private void handleException(Thread thread, String msg, Throwable throwable) throws IOException {
        //记录数量达到10个就清理数据
        File logDir = FileStorageUtil.getLogDir();
        if (logDir.exists()) {
            clearLogExMax(logDir);
        } else {
            logDir.mkdirs();
        }

        //写入错误信息到文件

        // 本次记录文件名
        Date date = new Date(); // 当前时间
        String logFileName = "Crash_" + formatter.format(date) + "_" + (throwable.getClass().getSimpleName()) + ".crashLog";
        Logg.e(TAG, logFileName);
        File logex = new File(logDir, logFileName);
        logex.createNewFile();
        // 写入异常到文件中
        FileWriter fw = new FileWriter(logex, true);
        fw.write("\r\n" + android.os.Build.BRAND + "," + android.os.Build.MODEL + "," + android.os.Build.VERSION.RELEASE); // 厂商,设备,以及版本信息
        fw.write("\r\nVersionCode: " + PackageManagerUtil.getVersion(mContext).first); //版本号
        fw.write("\r\nmsg：" + msg); // 记录的额外信息
        fw.write("\r\nProcess[" + mProcessName + "," + Process.myPid() + "]"); // 进程信息，线程信息
        fw.write("\r\n" + thread + "(" + thread.getId() + ")"); // 进程信息，线程信息
        fw.write("\r\nTime stamp：" + DateTimeUtil.formatDate(date, DateTimeUtil.DF_YYYY_MM_DD_HH_MM_SS)); // 日期
        fw.write("\r\n");
        // 打印调用栈
        PrintWriter printWriter = new PrintWriter(fw);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        fw.write("\r\n");
        fw.flush();
        printWriter.close();
        fw.close();
    }

    /**
     * 清理日志,限制日志数量.
     *
     * @param logDir 日志目录
     */
    private void clearLogExMax(File logDir) {
        File[] logList = logDir.listFiles(new FileNameSelector("txt", "crashLog"));
        if (logList == null || logList.length == 0) {
            return;
        }
        int length = logList.length;
        //保存10条
        if (length >= 10) {
            for (File aLogList : logList) {
                try {
                    if (aLogList.delete()) {
                        Logg.d(TAG, "clearLogExMax delete:" + aLogList.getName());
                    }
                } catch (Exception ex) {
                    Logg.e(TAG, "clearLogExMax,ex:" + ex);
                }
            }
        }
    }

    /**
     * 外部调用
     *
     * @param msg       记录的额外信息
     * @param throwable Throwable
     */
    public static void addCrash(String msg, Throwable throwable) {
        try {
            instance.get().handleException(Thread.currentThread(), msg, throwable);
        } catch (IOException e) {
            Logg.e(TAG, "uncaughtException,e:" + e.getMessage());
        }
    }
}
