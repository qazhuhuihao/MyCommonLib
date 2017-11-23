package cn.hhh.commonlib;

import android.app.Application;
import android.os.Looper;

import cn.hhh.commonlib.common.Configs;

/**
 * base应用程序入口
 * Created by hhh on 2017/3/15.
 */

public class MyBaseApplication extends Application {

    private long startTime;
    protected static MyBaseApplication application;
    private static android.os.Handler mMainThreadHandler;
    private static Looper mMainThreadLooper;
    private static Thread mMainThread;
    private static int mMainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        startTime = System.currentTimeMillis();
        initMyLogic();
    }

    private void initMyLogic() {
        mMainThreadLooper = getMainLooper();
        mMainThreadHandler = new android.os.Handler(mMainThreadLooper);
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();

        Configs.DEBUG = BuildConfig.DEBUG;
    }

    /**
     * 获取主线程Handler
     *
     * @return the mMainThreadHandler
     */
    public static android.os.Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 获取主线程轮询器
     *
     * @return the mMainThreadLooper
     */
    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    /**
     * 获取主线程
     *
     * @return the mMainThread
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 获取主线程ID
     *
     * @return the mMainThreadId
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public long getStartTime() {
        return startTime;
    }

//    public void restartApplication() {
//        System.out.println("重启app");
//        Intent intent = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//
//        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        //退出程序
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
//                restartIntent); // 1秒钟后重启应用
//
//        System.exit(0);
//    }

    public static MyBaseApplication getInstance() {
        return application;
    }
}
