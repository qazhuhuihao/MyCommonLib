package cn.hhh.commonlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import cn.hhh.commonlib.MyBaseApplication;
import cn.hhh.commonlib.R;


/**
 * function : UI工具类：包括常用功能：获取全局的上下文、主线程相关、主线程任务执行、资源id获取、资源获取、像数大小转换、自定义Toast.
 *
 * <p>Created by lzj on 2016/1/28.<p/>
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "DanglingJavadoc"})
public class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();

    /** 获取全局AppLike */
    public static MyBaseApplication getAppLike() {
        return MyBaseApplication.getInstance();
    }

    /** 获取全局上下文 */
    public static Context getContext() {
        return MyBaseApplication.getInstance();
    }

    /** 获取主线程 */
    public static Thread getMainThread() {
        return MyBaseApplication.getMainThread();
    }

    /** 获取主线程ID */
    public static long getMainThreadId() {
        return MyBaseApplication.getMainThreadId();
    }

    /** 获取主线程消息轮询器 */
    public static android.os.Looper getMainLooper() {
        return MyBaseApplication.getMainThreadLooper();
    }

    /** 获取主线程的handler */
    public static Handler getHandler() {
        return MyBaseApplication.getMainThreadHandler();
    }

    /** 在主线程中延时一定时间执行runnable */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /** 在主线程执行runnable */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /** 从主线程looper里面移除runnable */
    public static void removeCallbacksFromMainLooper(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    /** 判断当前的线程是否为主线程 */
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    /** 在主线程中运行任务 */
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            UIUtil.post(runnable);
        }
    }

    /** ----------------------根据资源id获取资源------start------------------------- */

    /**
     * 填充layout布局文件
     */
    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    /**
     * 获取Bitmap
     */
    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    /**
     * 获取颜色选择器
     */
    public static ColorStateList getColorStateList(int resId) {
        return ContextCompat.getColorStateList(getContext(), resId);
    }

    /** ----------------------根据资源id获取资源------end------------------------- */

    /** ----------------------px与dip相互转换------start------------------------- */
    /**
     * dip转换为px
     */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换为dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /** ----------------------px与dip相互转换------end------------------------- */

    /** ----------------------toast封装------start------------------------- */
    public static void showToastShort(final String msg) {
        showToastShort(msg, Gravity.CENTER);
    }

    public static void showToastShort(final String msg, final int gravity) {
        showToastShort(msg, Toast.LENGTH_SHORT, "#666666", 14, dip2px(5), gravity);
    }

    public static void showToastShort(final String msg, final int duration, final String bgColor,
                                      final int textSp, final int cornerRadius, final int gravity) {
        if (mToast != null && mToast.getView().getParent() != null) {
            return;
        }
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                buildToast(msg, duration, bgColor, textSp, cornerRadius, gravity).show();
            }
        });
    }

    private static Toast mToast;

    /**
     * 构造Toast
     *
     * @param msg          消息
     * @param duration     显示时间
     * @param bgColor      背景颜色
     * @param textSp       文字大小
     * @param cornerRadius 四边圆角弧度
     * @return Toast
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("ShowToast")
    private static Toast buildToast(String msg, int duration, String bgColor, int textSp, int cornerRadius, int gravity) {
        mToast = new Toast(getContext());
        mToast.setDuration(duration);
        mToast.setGravity(gravity, 0, Gravity.BOTTOM == gravity ? 150 : 0);
        // 设置Toast文字
        TextView tv = new TextView(getContext());
        int dpPaddingLR = dip2px(20);
        int dpPaddingTB = dip2px(7);
        tv.setPadding(dpPaddingLR, dpPaddingTB, dpPaddingLR, dpPaddingTB);
        tv.setGravity(Gravity.CENTER);
        tv.setText(msg);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSp);
        // Toast文字TextView容器
        LinearLayout mLayout = new LinearLayout(getContext());
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(bgColor));
        shape.setCornerRadius(cornerRadius);
        shape.setStroke(1, Color.parseColor(bgColor));
        shape.setAlpha(204);
        mLayout.setBackgroundDrawable(shape);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLayout.setLayoutParams(params);
        mLayout.setGravity(Gravity.CENTER);
        mLayout.addView(tv);
        // 将自定义View覆盖Toast的View
        mToast.setView(mLayout);
        return mToast;
    }
    /** ----------------------toast封装------end------------------------- */

    /** 颤抖控件 */
    public static void shakeView(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(UIUtil.getContext(), R.anim.shake));
    }

}
