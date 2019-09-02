package cn.hhh.mycommonlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import cn.hhh.commonlib.utils.Logg;
import cn.hhh.commonlib.utils.UIUtil;

/**
 * @author qazhu
 */
public class IpGetUtil {
    private static final String TAG = IpGetUtil.class.getSimpleName();

    @SuppressWarnings("ConstantConditions")
    public static String getIPAddress() {
        NetworkInfo info = ((ConnectivityManager) UIUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                Logg.d(TAG, "2G/3G/4G");
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (Throwable e) {
                    Logg.e(TAG, "", e);
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                Logg.d(TAG, "WIFI");
                WifiManager wifiManager = (WifiManager) UIUtil.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wifiManager != null;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intIP2StringIP(wifiInfo.getIpAddress());
            }
        } else {
            Logg.d(TAG, "No network");
            //当前无网络连接,请在设置中打开网络

        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip ip
     * @return ip
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
