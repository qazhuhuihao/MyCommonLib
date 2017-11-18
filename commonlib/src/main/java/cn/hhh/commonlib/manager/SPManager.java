package cn.hhh.commonlib.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.hhh.commonlib.utils.UIUtil;

/**
 * @author liujian
 */
@SuppressWarnings("unused")
public class SPManager {
    private static String TAG = SPManager.class.getSimpleName();
    private final static String SP_SELF_NAME = "config";

    private static SharedPreferences sp;

    private static void check() {
        if (null == sp)
            sp = UIUtil.getContext().getSharedPreferences(SP_SELF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存布尔值
     *
     * @param key   key
     * @param value value
     */
    public static void saveBoolean(String key, boolean value) {
        check();
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 保存字符串
     *
     * @param key   key
     * @param value value
     */
    public static void saveString(String key, String value) {
        check();
        sp.edit().putString(key, value).apply();

    }

    public static void clear() {
        check();
        sp.edit().clear().apply();
    }

    /**
     * 保存long型
     *
     * @param key   key
     * @param value value
     */
    public static void saveLong(String key, long value) {
        check();
        sp.edit().putLong(key, value).apply();
    }

    /**
     * 保存int型
     *
     * @param key   key
     * @param value value
     */
    public static void saveInt(String key, int value) {
        check();
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 保存float型
     * <p>
     * * @param context content
     *
     * @param key   key
     * @param value value
     */
    public static void saveFloat(String key, float value) {
        check();
        sp.edit().putFloat(key, value).apply();
    }

    /**
     * 获取字符值
     *
     * @param key      key
     * @param defValue defValue
     * @return String
     */
    public static String getString(String key, String defValue) {
        check();
        return sp.getString(key, defValue);
    }

    /**
     * 获取int值
     *
     * @param key      key
     * @param defValue defValue
     * @return int
     */
    public static int getInt(String key, int defValue) {
        check();
        return sp.getInt(key, defValue);
    }

    /**
     * 删除
     *
     * @param key key
     */
    public static void remove(String key) {
        check();
        sp.edit().remove(key).apply();
    }

    /**
     * 获取long值
     *
     * @param key      key
     * @param defValue defValue
     */
    public static long getLong(String key, long defValue) {
        check();
        return sp.getLong(key, defValue);
    }

    /**
     * 获取float值
     *
     * @param key      key
     * @param defValue defValue
     */
    public static float getFloat(String key, float defValue) {
        check();
        return sp.getFloat(key, defValue);
    }

    /**
     * 获取布尔值
     *
     * @param key      key
     * @param defValue defValue
     */
    public static boolean getBoolean(String key, boolean defValue) {
        check();
        return sp.getBoolean(key, defValue);
    }

    /**
     * 将对象进行base64编码后保存到SharePref中
     *
     * @param key    key
     * @param object object
     */
    public static void saveObj(String key, Object object) {
        check();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            // 将对象的转为base64码
            String objBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            sp.edit().putString(key, objBase64).apply();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将SharePref中经过base64编码的对象读取出来
     *
     * @param key key
     * @return T
     */
    public static <T> T getObj(String key, Class<T> cls) {
        check();
        String objBase64 = sp.getString(key, null);
        if (TextUtils.isEmpty(objBase64))
            return null;

        // 对Base64格式的字符串进行解码
        byte[] base64Bytes = Base64.decode(objBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);

        ObjectInputStream ois;
        T t = null;
        try {
            ois = new ObjectInputStream(bais);
            t = cls.cast(ois.readObject());
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}
