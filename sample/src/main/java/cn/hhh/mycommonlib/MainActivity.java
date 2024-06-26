package cn.hhh.mycommonlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

import cn.hhh.commonlib.CrashHandler;
import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.common.DeviceInfo;
import cn.hhh.commonlib.rx.ExConsumer;
import cn.hhh.commonlib.rx.RxBus;
import cn.hhh.commonlib.swlog.view.LogSuspensionWindow;
import cn.hhh.commonlib.utils.FileStorageUtil;
import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.UIUtil;
import cn.hhh.commonlib.xlog.XLogInit;
import cn.hhh.mycommonlib.bean.BaseBean;
import cn.hhh.mycommonlib.network.Network;
import io.reactivex.rxjava3.functions.Consumer;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * @author hhh
 */
public class MainActivity extends CommonBaseActivity implements EasyPermissions.PermissionCallbacks {

    private int i = 0;

    private final int REQUEST_CODE_SETTING = 300;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findView(R.id.tv);

// .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int[] a = null;
//                System.out.println(a[0]);
//            }
//        });

        tv.setOnClickListener(onClickListener);

        checkPermissions();

    }

    private void checkPermissions() {
//        AndPermission.with(this)
//                .runtime()
//                .permission(Permission.WRITE_EXTERNAL_STORAGE,
//                        Permission.READ_PHONE_STATE)
//                .onGranted(data -> {
//                    System.out.println("Granted");
//                    for (String datum : data) {
//                        System.out.println(datum);
//                    }
//                    next();
//                }).onDenied(data -> {
//            System.out.println("Denied");
//            for (String datum : data) {
//                System.out.println(datum);
//            }
//        })
//                .start();

//        if (AndPermission.hasPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE)) {
//            next();
//        } else {
//            AndPermission.with(this)
//                    .requestCode(REQUEST_CODE_SETTING)
//                    .permission(
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_PHONE_STATE)
//                    .callback(listener)
//                    .start();
//        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            next();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 1, perms)
                    .setRationale("需要权限")
                    .build());

        }
    }

    private void next() {
        //初始化日志记录
        XLogInit.init(UIUtil.getContext());

        //初始化应用文件目录
        FileStorageUtil.initAppDir();
        // 初始化设备信息
        DeviceInfo.init(this);
        //收集崩溃信息
        CrashHandler.init(UIUtil.getContext());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + UIUtil.getContext().getPackageName()));
                    startActivity(intent);
                }
            }
            //打开日志悬浮窗
            LogSuspensionWindow.getInstance().onCreate();
            for (int j = 0; j < 10; j++) {
                Logg.i(TAG, Integer.toString(i++));
            }
        } catch (Exception e) {
            UIUtil.showToastShort("没有悬浮窗权限");
            Logg.e(TAG, "", e);
        }

        String ip = IpGetUtil.getIPAddress();

        Logg.d(TAG, ip == null ? "null" : ip);

        PackageManager pm = getPackageManager();
        // 获取是否支持电话
        Logg.d("pm", pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY));
        // 是否支持GSM
        Logg.d("GSM", pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM));
        // 是否支持CDMA
        Logg.d("CDMA", pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA));
        /*
         * 使用hasSystemFeature方法可以检查设备是否其他功能。如陀螺仪，NFC，蓝牙等等，
         */
        Logg.d("NFC", pm.hasSystemFeature(PackageManager.FEATURE_NFC));
        Logg.d("GYROSCOPE", pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE));


    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = new Random().nextInt();
            RxBus.getDefault().post(i);
//            startActivityForResult(new Intent(MainActivity.this, BActivity.class), 1);
            myActivityLauncher.launch("Hello,技术最TOP");
//            sslTest();
        }
    };

    @SuppressLint("CheckResult")
    private void sslTest() {
        //noinspection ResultOfMethodCallIgnored
        Network
                .test()
                .subscribe(new Consumer<BaseBean>() {
                    @Override
                    public void accept(BaseBean baseBean) throws Exception {

                    }
                }, new ExConsumer());
    }

    @Override
    protected void onDestroy() {
        XLogInit.flush();

        super.onDestroy();
    }

    private final ActivityResultLauncher<String> myActivityLauncher = registerForActivityResult(new MyActivityResultContract(), result -> {
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        tv.setText("回传数据:+" + result);
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        next();
    }

    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 1, list.toArray(new String[0]))
                    .setRationale("需要权限")
                    .build());
        }
    }

    static class MyActivityResultContract extends ActivityResultContract<String, String> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            return new Intent(context, BActivity.class).putExtra("name", input);
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == RESULT_OK && intent != null)
                return intent.getStringExtra("result");
            else
                return null;
        }
    }
}
