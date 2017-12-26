package cn.hhh.mycommonlib;

import android.os.Bundle;
import android.util.Log;

import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.common.Configs;
import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;

/**
 * @author hhh
 */
public class MainActivity extends CommonBaseActivity {

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(Configs.DEBUG);
        LogSuspensionWindow.getInstance().onCreate();
        for (int j = 0; j < 200; j++) {
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.DEBUG, TAG, Integer.toString(i++)));
        }
    }
}
