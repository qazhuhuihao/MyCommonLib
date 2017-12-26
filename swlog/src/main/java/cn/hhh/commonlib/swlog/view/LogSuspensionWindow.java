package cn.hhh.commonlib.swlog.view;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hhh.commonlib.manager.AppManager;
import cn.hhh.commonlib.swlog.R;
import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.utils.SWLogg;
import cn.hhh.commonlib.utils.DateTimeUtil;
import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.UIUtil;

import static android.content.Context.WINDOW_SERVICE;

/**
 * @author qazhu
 * @date 2017/12/10
 */

public class LogSuspensionWindow {
    private final String TAG = this.getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static LogSuspensionWindow logSuspensionWindow;

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View rootView;
    private TextView tvTitle, tvZoom, tvLog;
    private HorizontalScrollView hsvLog;
    private ScrollView svLog;

    private int mStartX;
    private int mStartY;
    private int mDeviationX;
    private int mDeviationY;
    private int mEndX;
    private int mEndY;
    private int mWindowX;
    private int mWindowY;

    @SuppressWarnings("FieldCanBeLocal")
    private final int mMinMove = 30;

    private boolean mShow;
    private final int maxNumber = 150;

    private final int removeNumber = 60;

    private List<MyLogBean> myList = new ArrayList<>(maxNumber + removeNumber);
    private StringBuffer logBuffer = new StringBuffer();


    private LogSuspensionWindow() {
    }

    public static LogSuspensionWindow getInstance() {
        if (null == logSuspensionWindow)
            logSuspensionWindow = new LogSuspensionWindow();

        return logSuspensionWindow;
    }

    public void onCreate() {
        if (!Logg.getTAG().equals(SWLogg.class.getSimpleName())) {
            SWLogg.printToSW = true;
            Logg.setLogg(new SWLogg());
        }
        Logg.i(TAG, "onCreate");
        onDestroy();
        init();
    }

    @SuppressWarnings("all")
    public void onDestroy() {
        if (null != rootView) {
            //移除悬浮窗口
            Logg.i(TAG, "removeView");
            mWindowManager.removeView(rootView);
        }
        Logg.i(TAG, "onDestroy");

    }

    public void addLog(MyLogBean myLogBean) {
        boolean needRepaint = false;

        if (myList.size() >= maxNumber) {
            for (int i = 0; i < removeNumber; i++) {
                myList.remove(0);
            }
            needRepaint = true;
        }
        myList.add(myLogBean);
        if (needRepaint)
            repaintLog();
        else
            addLogBuffer(this.logBuffer, myLogBean);

        if (null != rootView) {
            UIUtil.post(new Runnable() {
                @Override
                public void run() {
                    tvLog.setText(logBuffer.toString());
                    if (tvLog.getMeasuredHeight() <= svLog.getScrollY() + svLog.getHeight())
                        UIUtil.post(new Runnable() {
                            @Override
                            public void run() {
                                svLog.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                }
            });

        }
    }

    private void init() {
        initWindowParams();
        initView();
        addWindowView2Window();
        initClick();
    }

    @SuppressLint("RtlHardcoded")
    private void initWindowParams() {
        mWindowManager = (WindowManager) UIUtil.getContext().getSystemService(WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        // 更多type：https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#TYPE_PHONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format = PixelFormat.TRANSLUCENT;
        // 更多falgs:https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_NOT_FOCUSABLE
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @SuppressLint("InflateParams")
    private void initView() {
        rootView = LayoutInflater.from(UIUtil.getContext()).inflate(R.layout.layout_suspension_window, null);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvZoom = rootView.findViewById(R.id.tv_zoom);
        tvLog = rootView.findViewById(R.id.tv_log);
        hsvLog = rootView.findViewById(R.id.hsv_log);
        svLog = rootView.findViewById(R.id.sv_log);
        setZoom(true);
    }

    private void setZoom(boolean isShow) {
        mShow = isShow;
        if (mShow) {
            hsvLog.setVisibility(View.VISIBLE);
            tvZoom.setText("-");
//            GradientDrawable titleDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_topleft_primary);
//            titleDrawable.setColor(UIUtil.getColor(R.color.black));
            tvTitle.setBackground(UIUtil.getDrawable(R.drawable.corners_topleft_primary));
            tvZoom.setBackground(UIUtil.getDrawable(R.drawable.corners_topright_primarydark));
        } else {
            hsvLog.setVisibility(View.GONE);
            tvZoom.setText("+");
            tvTitle.setBackground(UIUtil.getDrawable(R.drawable.corners_left_primary));
            tvZoom.setBackground(UIUtil.getDrawable(R.drawable.corners_right_primarydark));
        }
    }

    private void addWindowView2Window() {
        mWindowManager.addView(rootView, wmParams);
    }

    @SuppressWarnings("all")
    private void initClick() {
        tvTitle.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        mDeviationX = mStartX - mWindowX;
                        mDeviationY = mStartY - mWindowY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mEndX = (int) event.getRawX();
                        mEndY = (int) event.getRawY();
                        if (needIntercept()) {
                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            mWindowX = (int) event.getRawX() - mDeviationX;
                            mWindowY = (int) event.getRawY() - mDeviationY;
                            wmParams.x = mWindowX;
                            wmParams.y = mWindowY;
                            mWindowManager.updateViewLayout(rootView, wmParams);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (needIntercept()) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        tvTitle.setOnClickListener(onClickListener);
        tvZoom.setOnClickListener(onClickListener);

    }

    /**
     * 是否拦截
     *
     * @return true:拦截;false:不拦截.
     */
    private boolean needIntercept() {
        return Math.abs(mStartX - mEndX) > mMinMove || Math.abs(mStartY - mEndY) > mMinMove;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    @SuppressWarnings("all")
//    private boolean isAppAtBackground(final Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }
    private boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                System.out.println("processName:" + processInfo.processName);
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        System.out.println("activeProcess:" + activeProcess);
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }

        return isInBackground;
    }

    private void repaintLog() {
        this.logBuffer = new StringBuffer();
        for (MyLogBean myLogBean : myList) {
            addLogBuffer(this.logBuffer, myLogBean);
        }
    }

    private void addLogBuffer(StringBuffer logBuffer, MyLogBean logBean) {
        logBuffer.append(DateTimeUtil.formatDate(logBean.date, DateTimeUtil.DF_HH_MM_SS)).append(" ").append(logBean.logTag).append(" ").append(logBean.logMsg).append("\n");
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.tv_title) {
                Logg.i(TAG,"tv_title -> onClick");
                try {
                    if (isAppInBackground(UIUtil.getContext())) {
                        ActivityManager am = (ActivityManager) UIUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                        if (am != null) {
                            am.moveTaskToFront(AppManager.getAppManager().getTopActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                        }
                    }
                } catch (Exception e) {
                    Logg.e(TAG, "", e);
                }

            } else if (i == R.id.tv_zoom) {
                System.out.println("show:" + (!mShow));
                setZoom(!mShow);

            }
        }
    };
}
