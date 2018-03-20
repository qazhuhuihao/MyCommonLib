package cn.hhh.commonlib;

import android.app.Application;
import android.os.Looper;

import cn.hhh.commonlib.common.Configs;

/**
 * base应用程序入口
 *
 * @author hhh
 * @date 2017/3/15
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

    @SuppressWarnings("unused")
    public long getStartTime() {
        return startTime;
    }

    @SuppressWarnings("unchecked")
    public static <T extends MyBaseApplication> T getInstance() {
        return (T) application;
    }
}
