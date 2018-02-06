package cn.hhh.commonlib.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.hhh.commonlib.utils.UIUtil;

/**
 * @author lxm
 * Activity管理类：用于管理Activity和退出程序
 */
@SuppressWarnings("unused")
public class AppManager {

    /*** 寄存整个应用Activity **/
    private final Stack<WeakReference<Activity>> activityStack = new Stack<>();

    /*** 单例模式 **/
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 将Activity压入Application栈
     *
     * @param task 将要压入栈的Activity对象
     */
    public void pushTask(WeakReference<Activity> task) {
        activityStack.push(task);
    }

    /**
     * 将传入的Activity对象从栈中移除
     *
     * @param task 将要移除栈的Activity对象
     */
    public void removeTask(WeakReference<Activity> task) {
        activityStack.remove(task);
    }

    /**
     * 关闭某个activity
     *
     * @param activityCls 指定activity的类 eg：MainActivity.class
     */
    public void finishActivity(Class<? extends Activity> activityCls) {
        int end = activityStack.size();
        for (int i = end - 1; i >= 0; i--) {
            Activity cacheActivity = activityStack.get(i).get();
            if (cacheActivity.getClass().getSimpleName().equals(activityCls.getSimpleName()) && !cacheActivity.isFinishing()) {
                cacheActivity.finish();
                removeTask(i);
            }
        }
    }

    /**
     * 根据指定位置从栈中移除Activity
     *
     * @param taskIndex Activity栈索引
     */
    private void removeTask(int taskIndex) {
        if (activityStack.size() > taskIndex)
            activityStack.remove(taskIndex);
    }

    /**
     * 获取顶层activity
     */
    public Activity getTopActivity() {
        if (activityStack.size() > 0) {
            return activityStack.get(activityStack.size() - 1).get();
        }
        return null;
    }

    /**
     * 移除全部（用于整个应用退出）
     */
    public void removeAll() {
        int end = activityStack.size();
        for (int i = end - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i).get();
            if (null != activity && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityStack.clear();
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 1000);
    }

    /**
     * 移除除最后一个Activity之外的全部（主要用于启动MainActivity）
     */
    public void removeAllExceptLast() {
        int end = activityStack.size();
        for (int i = end - 2; i >= 0; i--) {
            Activity activity = activityStack.get(i).get();
            if (null != activity && !activity.isFinishing()) {
                activity.finish();
            }
            removeTask(i);
        }
    }

    public void finishOtherActivity(WeakReference<Activity> task) {
        List<WeakReference<Activity>> activityList = new ArrayList<>();

        for (WeakReference<Activity> activityWeakReference : activityStack) {
            if (null != activityWeakReference && !activityWeakReference.equals(task)) {
                activityList.add(activityWeakReference);
                Activity cacheActivity = activityWeakReference.get();
                if (null != cacheActivity && !cacheActivity.isFinishing()) {
                    cacheActivity.finish();
                }
            }
        }

        activityStack.removeAll(activityList);
    }

    /**
     * 移除除第一个Activity之外的全部（主要用于回到MainActivity）
     */
    public void removeAllExceptFirst() {
        int end = activityStack.size();
        for (int i = end - 1; i >= 1; i--) {
            Activity activity = activityStack.get(i).get();
            if (null != activity && !activity.isFinishing()) {
                activity.finish();
            }
            removeTask(i);
        }
    }

}