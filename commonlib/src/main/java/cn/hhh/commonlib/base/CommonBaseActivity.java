package cn.hhh.commonlib.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import cn.hhh.commonlib.common.DeviceInfo;
import cn.hhh.commonlib.manager.AppManager;
import cn.hhh.commonlib.utils.Logg;

/**
 * function:android 系统中的四大组件之一Activity基类.
 * <p></p>
 *
 * @author hhh
 * @date 2017/3/22
 */
@SuppressWarnings({"unused"})
public class CommonBaseActivity extends AppCompatActivity {
    /**
     * 日志输出标志,当前类的类名
     */
    protected final String TAG = this.getClass().getSimpleName();

    public static int activityCount;

    /**
     * 当前Activity的弱引用，防止内存泄露
     */
    protected WeakReference<Activity> activityWeakReference = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logg.d(TAG, "-->onCreate()");
        // 将当前Activity压入栈
        activityWeakReference = new WeakReference<Activity>(this);
        AppManager.getAppManager().pushTask(activityWeakReference);
    }

    @Override
    protected void onRestart() {
        Logg.d(TAG, "-->onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Logg.d(TAG, "-->onStart()");
        activityCount++;
        super.onStart();
    }

    @Override
    protected void onResume() {
        Logg.d(TAG, "-->onResume()");
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logg.d(TAG, "-->onActivityResult(requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + (data == null ? "null" : data.toString()) + ")");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Logg.d(TAG, "-->onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Logg.d(TAG, "-->onStop()");
        activityCount--;
        super.onStop();
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        } else {
            return mDestroyed;
        }
    }

    private boolean mDestroyed;

    @Override
    protected void onDestroy() {
        Logg.d(TAG, "-->onDestroy()");
        if (activityWeakReference != null) {
            AppManager.getAppManager().removeTask(activityWeakReference);
        }
        super.onDestroy();
        mDestroyed = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Logg.d(TAG, "-->onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        DeviceInfo.ScreenHeightPixels = dm.heightPixels;// 高
        DeviceInfo.ScreenWidthPixels = dm.widthPixels;// 宽
    }

    protected final <T extends View> T findView(int id) {
        return this.findViewById(id);
    }

    protected final <T extends View> T findView(View view, int id) {
        return view.findViewById(id);
    }

    /**
     * 一般在{@link #onDestroy()}时调用
     */
    private void releaseHandlers() {
        try {
            Class<?> clazz = getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields == null || fields.length <= 0) {
                return;
            }
            for (Field field : fields) {
                field.setAccessible(true);
                if (!Handler.class.isAssignableFrom(field.getType())) continue;
                Handler handler = (Handler) field.get(this);
                if (handler != null && handler.getLooper() == Looper.getMainLooper()) {
                    handler.removeCallbacksAndMessages(null);
                }
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
