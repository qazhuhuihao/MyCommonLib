package cn.hhh.mycommonlib;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.hhh.commonlib.CrashHandler;
import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.common.DeviceInfo;
import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;
import cn.hhh.commonlib.utils.FileStorageUtil;
import cn.hhh.commonlib.utils.UIUtil;

/**
 * @author hhh
 */
public class MainActivity extends CommonBaseActivity {

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化应用文件目录
        FileStorageUtil.initAppDir();
        // 初始化设备信息
        DeviceInfo.init(this);
        CrashHandler.init(UIUtil.getContext());

        LogSuspensionWindow.getInstance().onCreate();
        for (int j = 0; j < 200; j++) {
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.DEBUG, TAG, Integer.toString(i++)));
        }

        findView(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] a = null;
                System.out.println(a[0]);
            }
        });
    }
}
