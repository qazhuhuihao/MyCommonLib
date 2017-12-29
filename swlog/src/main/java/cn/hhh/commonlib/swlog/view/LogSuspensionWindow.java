package cn.hhh.commonlib.swlog.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.widget.CardView;
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
import cn.hhh.commonlib.utils.PackageManagerUtil;
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
    private CardView cvRoot;
    private TextView tvTitle, tvZoom, tvLog;
    private HorizontalScrollView hsvLog;
    private ScrollView svLog;

    private int rotateAnimationDuration = 2000;
    //    private Animation circularAnimation;
    //private AnimatorSet animatorSet;
    private ValueAnimator animator;

    private int cRadius;
    private int cTitleHeight;
    private int cTitleWidth;
    private int cZoomWidth;

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

    private int zoomLevel;
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
        getConfigure();
        //initAnimation();
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
        cvRoot = rootView.findViewById(R.id.cv_root);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvZoom = rootView.findViewById(R.id.tv_zoom);
        tvLog = rootView.findViewById(R.id.tv_log);
        hsvLog = rootView.findViewById(R.id.hsv_log);
        svLog = rootView.findViewById(R.id.sv_log);
        setZoom(1);
    }

    private void setZoom(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        Logg.i(TAG, "zoomLevel:" + zoomLevel);
        if (0 < zoomLevel) {
            hsvLog.setVisibility(View.VISIBLE);
            tvZoom.setText("-");
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
//        tvTitle.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        mStartX = (int) event.getRawX();
//                        mStartY = (int) event.getRawY();
//                        mDeviationX = mStartX - mWindowX;
//                        mDeviationY = mStartY - mWindowY;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        mEndX = (int) event.getRawX();
//                        mEndY = (int) event.getRawY();
//                        if (needIntercept()) {
//                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
//                            mWindowX = (int) event.getRawX() - mDeviationX;
//                            mWindowY = (int) event.getRawY() - mDeviationY;
//                            wmParams.x = mWindowX;
//                            wmParams.y = mWindowY;
//                            mWindowManager.updateViewLayout(rootView, wmParams);
//                            return true;
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (needIntercept()) {
//                            return true;
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
        tvTitle.setOnTouchListener(onTouchListener);
        //tvZoom.setOnTouchListener(onTouchListener);
        tvTitle.setOnClickListener(onClickListener);
        tvZoom.setOnClickListener(onClickListener);

    }

    private void getConfigure() {
        cRadius = UIUtil.getDimens(R.dimen.cardview_default_radius);
        cTitleHeight = UIUtil.getDimens(R.dimen.sw_title_height);
        cTitleWidth = UIUtil.getDimens(R.dimen.sw_title_width);
        cZoomWidth = UIUtil.getDimens(R.dimen.sw_zoom_width);
    }

    private void initAnimation() {
//        circularAnimation = new CircularAnimation();
        //animatorSet = new AnimatorSet();
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(rotateAnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();

                if (zoomLevel != 0)
                    progress = 1 - progress;

                System.out.println(progress);
                //float radius = ((float) cTitleHeight) / 2 - cRadius * progress;
                float radius = cRadius + (cTitleHeight / 2 - cRadius) * progress;
                System.out.println(radius);
//                GradientDrawable titleDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_left_primary);
//                GradientDrawable zoomDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_right_primarydark);
//                float[] radiiLeft = new float[]{radius, radius, 0, 0, 0, 0, radius, radius};
//                float[] radiiRight = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
//                titleDrawable.setCornerRadii(radiiLeft);
//                zoomDrawable.setCornerRadii(radiiRight);

                int width = (int) (cZoomWidth - (cZoomWidth - cTitleHeight) * progress);
                tvZoom.setWidth(width);
                tvZoom.postInvalidate();
                tvTitle.setWidth((int) ((1 - progress) * cTitleWidth));
                tvTitle.postInvalidate();

                cvRoot.setRadius(radius);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Logg.i(TAG, "onAnimationStart()");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Logg.i(TAG, "onAnimationEnd()");
            }
        });

    }

    /**
     * 是否拦截
     *
     * @return true:拦截;false:不拦截.
     */
    private boolean needIntercept() {
        return Math.abs(mStartX - mEndX) > mMinMove || Math.abs(mStartY - mEndY) > mMinMove;
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
                Logg.i(TAG, "tv_title -> onClick");
                try {
                    if (PackageManagerUtil.isAppOnForeground(UIUtil.getContext())) {
                        ActivityManager am = (ActivityManager) UIUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                        if (am != null) {
                            am.moveTaskToFront(AppManager.getAppManager().getTopActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                        }
                    }
                } catch (Exception e) {
                    Logg.e(TAG, "", e);
                }

            } else if (i == R.id.tv_zoom) {
                Logg.i(TAG, "tv_zoom -> onClick");
                setZoom(1 - zoomLevel);
//                if (2 > zoomLevel) {
//                    System.out.println("startCircularAnimation()");
//                    startCircularAnimation();
//                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            if (v.getId() == tvZoom.getId() && zoomLevel > 0)
//                return false;

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
    };

    private void startCircularAnimation() {
        animator.cancel();
        animator.start();
    }

}
