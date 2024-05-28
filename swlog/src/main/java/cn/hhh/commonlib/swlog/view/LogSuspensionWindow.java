package cn.hhh.commonlib.swlog.view;

import static android.content.Context.WINDOW_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import cn.hhh.commonlib.manager.AppManager;
import cn.hhh.commonlib.swlog.R;
import cn.hhh.commonlib.swlog.activity.CrashActivity;
import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.utils.SWLogg;
import cn.hhh.commonlib.utils.DateTimeUtil;
import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.PackageManagerUtil;
import cn.hhh.commonlib.utils.UIUtil;

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
    private ViewGroup llTitle;
    private TextView tvTitle, tvZoom, tvLog;
    private HorizontalScrollView hsvLog;
    private ScrollView svLog;
    private ImageView ivZoom;

    @SuppressWarnings("FieldCanBeLocal")
    private int zoomDuration = 1200;
    private int zeDuration = 300;
    private ValueAnimator zoomAnimator, ceAnimator;
    private TimeInterpolator mInterpolator;

    private int windowWidth;
    private int windowHeight;

    private int cRadius;
    private int cTitleHeight;
    private int cTitleWidth;
    private int cZoomWidth;

    /**
     * 悬浮窗移动需要的变量
     */
    private int mStartX;
    private int mStartY;
    private int mDeviationX;
    private int mDeviationY;
    private int mEndX;
    private int mEndY;
    private int mWindowX;
    private int mWindowY;

    /**
     * 悬浮窗缩放需要的变量
     */
    private boolean isIvZoomDown;
    private boolean ivZoomLongDown;
    private int mStartHeight, mStartWidth;
    private ViewGroup.LayoutParams llTitleLayoutParams;
    private ViewGroup.LayoutParams tvTitleLayoutParams;
    private ViewGroup.LayoutParams hsvLogLayoutParams;
    private ViewGroup.LayoutParams svLogLayoutParams;
    private ViewGroup.MarginLayoutParams tvZoomLayoutParams;

    @SuppressWarnings("FieldCanBeLocal")
    private final int mMinMove = 30;
    private boolean isMoving = false;

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
        Logg.addLogg(SWLogg.getInstance());
        SWLogg.printToSW = true;
        onDestroy();
        Logg.i(TAG, "onCreate");
        init();
    }

    public void onDestroy() {
        try {
            if (null != rootView) {
                //移除悬浮窗口
                Logg.i(TAG, "removeView");
                mWindowManager.removeView(rootView);
                rootView = null;
            }
            Logg.i(TAG, "onDestroy");
        } catch (Exception e) {
            Logg.e(TAG, "onDestroy", e);
        }

    }

    public void addLog(MyLogBean myLogBean) {
        boolean needRepaint = false;

        if (myList.size() >= maxNumber) {
            myList.subList(0, removeNumber).clear();
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
        initAnimation();

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
        llTitle = rootView.findViewById(R.id.ll_title);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvZoom = rootView.findViewById(R.id.tv_zoom);
        tvLog = rootView.findViewById(R.id.tv_log);
        hsvLog = rootView.findViewById(R.id.hsv_log);
        svLog = rootView.findViewById(R.id.sv_log);
        ivZoom = rootView.findViewById(R.id.iv_zoom);

        llTitleLayoutParams = llTitle.getLayoutParams();
        tvTitleLayoutParams = tvTitle.getLayoutParams();
        hsvLogLayoutParams = hsvLog.getLayoutParams();
        svLogLayoutParams = svLog.getLayoutParams();
        tvZoomLayoutParams = (ViewGroup.MarginLayoutParams) tvZoom.getLayoutParams();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            cvRoot.setCardElevation(0);
        }

        setZoom(1);
    }

    private void setZoom(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        if (0 < zoomLevel) {
//            hsvLog.setVisibility(View.VISIBLE);
            cvRoot.setVisibility(View.VISIBLE);
            tvZoom.setText("-");
            tvTitle.setBackground(UIUtil.getDrawable(R.drawable.corners_topleft_primary));
            tvZoom.setBackground(UIUtil.getDrawable(R.drawable.corners_topright_primarydark));
            ivZoom.setVisibility(View.VISIBLE);
            ivZoom.setImageAlpha(25);
        } else {
//            hsvLog.setVisibility(View.GONE);
            cvRoot.setVisibility(View.GONE);
            tvZoom.setText("+");
            tvTitle.setBackground(UIUtil.getDrawable(R.drawable.corners_left_primary));
            tvZoom.setBackground(UIUtil.getDrawable(R.drawable.corners_right_primarydark));
            ivZoom.setVisibility(View.GONE);

        }
    }

    private void addWindowView2Window() {
        mWindowManager.addView(rootView, wmParams);
        mWindowX = 0;
        mWindowY = 0;
    }

    private void initClick() {
        tvTitle.setOnTouchListener(onTouchListener);
        tvZoom.setOnTouchListener(onTouchListener);
        ivZoom.setOnTouchListener(ivZoomOnTouch);
        tvTitle.setOnClickListener(onClickListener);
        tvZoom.setOnClickListener(onClickListener);

        tvTitle.setOnLongClickListener(onLongClickListener);
        tvZoom.setOnLongClickListener(onLongClickListener);
    }

    private void getConfigure() {
        cRadius = UIUtil.getDimens(R.dimen.cardview_default_radius);
        cTitleHeight = UIUtil.getDimens(R.dimen.sw_title_height);
        cTitleWidth = UIUtil.getDimens(R.dimen.sw_title_width);
        cZoomWidth = UIUtil.getDimens(R.dimen.sw_zoom_width);

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        windowWidth = point.x;
        windowHeight = point.y;
    }

    private void initAnimation() {
        zoomAnimator = ValueAnimator.ofFloat(0, 1);
        zoomAnimator.setDuration(zoomDuration);
        zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();

                if (zoomLevel != 0)
                    progress = 1 - progress;

                int titleWidth = (int) ((1 - progress) * cTitleWidth);

                int radius = UIUtil.dip2px(cRadius + (UIUtil.px2dip(cTitleHeight) / 2 - cRadius) * progress);

//                int y = titleWidth * 2 < cTitleHeight ? (Math.min((cTitleHeight - titleWidth * 2), radius)) : 0;

                GradientDrawable titleDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_left_primary);
                GradientDrawable zoomDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_right_primarydark);
                float[] radiiLeft = new float[]{radius, radius, 0, 0, 0, 0, radius, radius};
                float[] radiiRight = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};

                tvTitle.getLayoutParams().height = cTitleHeight;

                titleDrawable.setCornerRadii(radiiLeft);
                zoomDrawable.setCornerRadii(radiiRight);

                tvZoom.getLayoutParams().width = (int) (cZoomWidth - (cZoomWidth - cTitleHeight) * progress);

                tvZoomLayoutParams.setMargins(titleWidth, 0, 0, 0);

                tvTitle.getLayoutParams().width = titleWidth + cTitleHeight / 2;

//                tvTitle.getLayoutParams().height = Math.min(titleWidth * 2, cTitleHeight);

                cvRoot.setRadius(radius);
                tvTitle.requestLayout();
                tvZoom.requestLayout();

            }
        });

        zoomAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                llTitle.getLayoutParams().width = cZoomWidth + cTitleWidth;
                llTitle.requestLayout();
                if (1 == zoomLevel) {
                    int radius = UIUtil.dip2px(UIUtil.px2dip(cTitleHeight) / 2);
                    float[] radiiRight = new float[]{0, 0, radius, radius, radius, radius, 0, 0};
                    GradientDrawable zoomDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_right_primarydark);
                    zoomDrawable.setCornerRadii(radiiRight);
                    tvZoom.setBackgroundResource(R.drawable.corners_right_primarydark);
                }
            }

            @Override

            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (1 == zoomLevel) {
                    setZoom(zoomLevel);
                } else if (0 == zoomLevel) {

                    int radius = UIUtil.dip2px(UIUtil.px2dip(cTitleHeight) / 2);

                    float[] radiiRight = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                    GradientDrawable zoomDrawable = (GradientDrawable) UIUtil.getDrawable(R.drawable.corners_right_primarydark);
                    zoomDrawable.setCornerRadii(radiiRight);
                    tvZoom.setBackgroundResource(R.drawable.corners_right_primarydark);

                    llTitle.getLayoutParams().width = cTitleHeight;
                    llTitle.requestLayout();

                    int startX = wmParams.x;
                    int endX = (startX * 2 + cZoomWidth > windowWidth) ? windowWidth : 0;
                    ceAnimator = ObjectAnimator.ofInt(startX, endX);
                    ceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            wmParams.x = (int) animation.getAnimatedValue();
                            mWindowManager.updateViewLayout(rootView, wmParams);
                        }
                    });
                    if (null == mInterpolator)
                        mInterpolator = new DecelerateInterpolator();

                    ceAnimator.setInterpolator(mInterpolator);
                    ceAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ceAnimator.removeAllUpdateListeners();
                            ceAnimator.removeAllListeners();
                            ceAnimator = null;
                        }
                    });
                    ceAnimator.setDuration(zeDuration).start();

                }
            }
        });
        cvRoot.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        cvRoot.setPersistentDrawingCache(ViewGroup.PERSISTENT_NO_CACHE);
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
                if (0 == zoomLevel) {
                    zoomLevel = 1 - zoomLevel;
                } else {
                    setZoom(1 - zoomLevel);
                }
                if (2 > zoomLevel) {
                    startZoomAnimation();
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == tvZoom.getId() && zoomLevel > 0)
                return false;

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
                        isMoving = true;
                        //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        mWindowX = (int) event.getRawX() - mDeviationX;
                        mWindowY = (int) event.getRawY() - mDeviationY;
                        switch (zoomLevel) {
                            case 0:
                                if (mWindowX > (windowWidth - cTitleHeight))
                                    mWindowX = windowWidth - cTitleHeight;
                                break;
                            case 1:
                                if (mWindowX > (windowWidth - cZoomWidth - cTitleWidth))
                                    mWindowX = windowWidth - cZoomWidth - cTitleWidth;
                                break;
                            default:
                        }
                        wmParams.x = mWindowX;
                        wmParams.y = mWindowY;
                        mWindowManager.updateViewLayout(rootView, wmParams);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    mEndX = (int) event.getRawX();
                    mEndY = (int) event.getRawY();
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

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (isMoving)
                return false;
            int i = v.getId();
            if (i == R.id.tv_title) {
                if (!CrashActivity.isCreate) {
                    Intent intent = new Intent(UIUtil.getContext(), CrashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UIUtil.getContext().startActivity(intent);
                    return true;
                }
            } else return i == R.id.tv_zoom;

            return false;
        }
    };

    private View.OnTouchListener ivZoomOnTouch = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isIvZoomDown = true;
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isIvZoomDown) {
                                ivZoomLongDown = true;
                                ivZoom.setImageAlpha(255);
                                mStartWidth = tvTitleLayoutParams.width - cTitleHeight / 2;
                                mStartHeight = svLogLayoutParams.height;
                            }
                        }
                    }, 1000);
                    mStartX = (int) event.getRawX();
                    mStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mEndX = (int) event.getRawX();
                    mEndY = (int) event.getRawY();
                    if (isIvZoomDown) {
                        if (!ivZoomLongDown && needIntercept()) {
                            isIvZoomDown = false;
                        } else if (ivZoomLongDown) {
                            int baseWidth = mStartWidth + mEndX - mStartX;
                            int baseHeight = mStartHeight + mEndY - mStartY;
                            if (baseWidth < cTitleHeight)
                                baseWidth = cTitleHeight;
                            if (baseHeight < cTitleHeight)
                                baseHeight = cTitleHeight;

                            if (baseHeight > windowHeight - cTitleHeight * 2)
                                baseHeight = windowHeight - cTitleHeight * 2;

                            if (baseWidth > windowWidth - cZoomWidth)
                                baseWidth = windowWidth - cZoomWidth;

                            cTitleWidth = baseWidth;

                            llTitleLayoutParams.width = baseWidth + cZoomWidth;
                            tvTitleLayoutParams.width = baseWidth + cTitleHeight / 2;
                            hsvLogLayoutParams.width = baseWidth + cZoomWidth;
                            svLogLayoutParams.height = baseHeight;

                            llTitle.setLayoutParams(llTitleLayoutParams);
                            tvTitle.setLayoutParams(tvTitleLayoutParams);
                            hsvLog.setLayoutParams(hsvLogLayoutParams);
                            svLog.setLayoutParams(svLogLayoutParams);

                            tvZoomLayoutParams.setMargins(baseWidth, 0, 0, 0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isIvZoomDown = false;
                    ivZoomLongDown = false;
                    ivZoom.setImageAlpha(25);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void startZoomAnimation() {
        zoomAnimator.cancel();
        zoomAnimator.start();
    }

}
