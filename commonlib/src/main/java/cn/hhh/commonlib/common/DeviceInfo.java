package cn.hhh.commonlib.common;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.UIUtil;


/**
 * function : 本地设备信息配置 : 包含设备屏幕信息、系统语言、设备唯一ID.
 * <p>
 * <p></p>
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 * <uses-permission name="android.permission.ACCESS_WIFI_STATE"/>
 * <p>
 * Created by lzj on 2015/12/31.
 */
@SuppressLint({"HardwareIds"})
@SuppressWarnings("WeakerAccess")
public final class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getSimpleName();
    /**
     * 屏幕高度
     */
    public static int ScreenHeightPixels = -1;
    /**
     * 屏幕宽度
     */
    public static int ScreenWidthPixels = -1;
    /**
     * 屏幕密度
     */
    public static float ScreenDensity = -1;
    /**
     * 屏幕密度
     */
    public static int ScreenDensityDpi = -1;
    /**
     * 屏幕字体密度
     */
    public static float ScreenScaledDensity = -1;
//    /**
//     * 初次运行时系统语言环境
//     */
//    public static String systemLastLocale = null;
    /**
     * 当前设备的IMEI
     */
    public static String deviceIMEI;
    /**
     * 当前设备的MAC地址
     */
    public static String deviceMAC;
    /**
     * 该设备在此程序上的唯一标识符
     */
    public static String deviceUUID;

    private static final String INSTALLATION = "INSTALLATION-" + UUID.nameUUIDFromBytes("androidkit".getBytes());

    /**
     * 初始化本地配置
     */
    @SuppressWarnings("all")
    public static void init(Context context) {
        initDisplayMetrics(context);
        //systemLastLocale = Locale.getDefault().toString();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceIMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
            } else {
                deviceIMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }
        } catch (Exception e) {
            Logg.e(TAG, "", e);
        }
        initMacAddress(context);
        initDevicesID(context);

        printfDeviceInfo();
    }

    /**
     * 屏幕信息获取,使用Resources获取
     */
    private static void initDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScreenHeightPixels = dm.heightPixels;// 高
        ScreenWidthPixels = dm.widthPixels;// 宽
        ScreenDensity = dm.density;// 密度
        ScreenDensityDpi = dm.densityDpi;//
        ScreenScaledDensity = dm.scaledDensity;
    }

    private static void printfDeviceInfo() {
        Logg.i(TAG, "----------DeviceInfo start----------");
        Logg.i(TAG, "DEBUG:", Configs.DEBUG);
        Logg.i(TAG, "Device:", android.os.Build.BRAND, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE);
        Logg.i(TAG, "ScreenHeightPixels:", ScreenHeightPixels, ", ScreenWidthPixels:", ScreenWidthPixels);
        Logg.i(TAG, "ScreenDensity:", ScreenDensity, ", ScreenDensityDpi:", ScreenDensityDpi, ", ScreenScaledDensity:", ScreenScaledDensity);
        Logg.i(TAG, "systemLastLocale:", Locale.getDefault().toString());
        Logg.i(TAG, "deviceIMEI:", deviceIMEI, " , deviceMAC:", deviceMAC, " , deviceUUID:", deviceUUID);
        Logg.i(TAG, "totalMem:", getmem_TOTAL(), " , unusedMem:", getmem_UNUSED(UIUtil.getContext()));
        Logg.i(TAG, "----------DeviceInfo end----------");
    }

    /**
     * 获取mac地址
     */
    private static void initMacAddress(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null && manager.getConnectionInfo() != null) {
            deviceMAC = manager.getConnectionInfo().getMacAddress();
        }
        if ("02:00:00:00:00:00".equals(deviceMAC) || TextUtils.isEmpty(deviceMAC)) {
            deviceMAC = getMacAddrInner();
        }
    }

    private static String getMacAddrInner() {
        String defaulMac = "02:00:00:00:00:00";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!"wlan0".equalsIgnoreCase(nif.getName())) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return defaulMac;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                defaulMac = res1.toString();
                return defaulMac;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return defaulMac;
    }

    /**
     * 返回该设备在此程序上的唯一标识符。
     */
    public static void initDevicesID(Context context) {
        if (deviceUUID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(context, installation);
                }
                deviceUUID = readInstallationFile(installation);
                if (!TextUtils.isEmpty(deviceUUID)) {
                    deviceUUID = deviceUUID.replace("-", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将表示此设备在该程序上的唯一标识符写入程序文件系统中。
     *
     * @param installation 保存唯一标识符的File对象。
     * @return 唯一标识符。
     */
    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(installation, "r");
        byte[] bs = new byte[(int) accessFile.length()];
        accessFile.readFully(bs);
        accessFile.close();
        return new String(bs);
    }

    /**
     * 读出保存在程序文件系统中的表示该设备在此程序上的唯一标识符。
     *
     * @param installation 保存唯一标识符的File对象。
     */
    private static void writeInstallationFile(Context context, File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String uuid = UUID.nameUUIDFromBytes(Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes()).toString();
        out.write(uuid.getBytes());
        out.close();
    }

    /**
     * 获得可用的内存
     */
    public static long getmem_UNUSED(Context mContext) {
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        assert am != null;
        am.getMemoryInfo(mi);

        // 取得剩余的内存空间

        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    /**
     * 获得总内存
     */
    public static long getmem_TOTAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        assert content != null;
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息
        System.out.println(content);
        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }
}