package cn.hhh.commonlib.base;

import android.app.Service;
import android.content.Intent;

import cn.hhh.commonlib.utils.Logg;

/**
 * function : android 系统中的四大组件之一Service基类.
 * <p></p>
 * Created by lzj on 2015/12/31.
 */
public abstract class CommonBaseService extends Service {

    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        Logg.d(TAG, "-->onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logg.d(TAG, "-->onStartCommand(intent:" + intent + ", flags:" + flags + ", startId:" + startId + ")");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logg.d(TAG, "-->onDestroy()");
        super.onDestroy();
    }

}
