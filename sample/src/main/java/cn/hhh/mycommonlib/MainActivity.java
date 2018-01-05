package cn.hhh.mycommonlib;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import cn.hhh.commonlib.CrashHandler;
import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.common.DeviceInfo;
import cn.hhh.commonlib.manager.AppManager;
import cn.hhh.commonlib.swlog.bean.MyLogBean;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;
import cn.hhh.commonlib.utils.FileStorageUtil;
import cn.hhh.commonlib.utils.UIUtil;

/**
 * @author hhh
 */
public class MainActivity extends CommonBaseActivity {

    private int i = 0;

    private final int REQUEST_CODE_SETTING = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] a = null;
                System.out.println(a[0]);
            }
        });

        checkPermissions();

    }

    private void checkPermissions() {
        if (AndPermission.hasPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)) {
            next();
        } else {
            AndPermission.with(this)
                    .requestCode(REQUEST_CODE_SETTING)
                    .permission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE)
                    .callback(listener)
                    .start();
        }
    }

    private void next() {
        //初始化应用文件目录
        FileStorageUtil.initAppDir();
        // 初始化设备信息
        DeviceInfo.init(this);
        CrashHandler.init(UIUtil.getContext());

        LogSuspensionWindow.getInstance().onCreate();
        for (int j = 0; j < 200; j++) {
            LogSuspensionWindow.getInstance().addLog(new MyLogBean(Log.DEBUG, TAG, Integer.toString(i++)));
        }

    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int i, @NonNull List<String> list) {

            next();
        }

        @Override
        public void onFailed(int i, @NonNull List<String> list) {
            AndPermission.defaultSettingDialog(MainActivity.this, REQUEST_CODE_SETTING)
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppManager.getAppManager().removeAll();
                        }
                    })
                    .show();
        }
    };
}
