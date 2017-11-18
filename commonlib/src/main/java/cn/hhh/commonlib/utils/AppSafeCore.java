package cn.hhh.commonlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 应用有效性检测
 * Created by hhh on 2017/6/6.
 */

public final class AppSafeCore {

    private static final String TAG = AppSafeCore.class.getSimpleName();

    private AppSafeCore() {
    }

    private static WeakReference<Context> mWeakReference;

    /**
     * 安装
     */
    public static void inStall(Context context) {
        mWeakReference = new WeakReference<>(context);
    }

    /**
     * 检查合法性
     */
    public static boolean checkLegal(String date) {
        long currentTime = System.currentTimeMillis();
        long exceptedTime = parseDate(date) + 86400000;
        boolean illegal = currentTime >= exceptedTime;
        Logg.d(TAG, "illegal:" + illegal);
        Logg.d(TAG, "currentTime=" + currentTime + ",exceptedTime=" + exceptedTime);
        if (illegal)
            feedbackLegalInfo("The application has expired, please contact the developer");

        return !illegal;
    }

    private static void feedbackLegalInfo(String msg) {
        if (mWeakReference == null || mWeakReference.get() == null) return;
        Toast.makeText(mWeakReference.get(), msg, Toast.LENGTH_LONG).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 3500);
    }

    @SuppressLint("SimpleDateFormat")
    private static long parseDate(String strDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(strDate).getTime();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }
}
