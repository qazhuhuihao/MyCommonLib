package cn.hhh.commonlib.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import cn.hhh.commonlib.CrashHandler;

/**
 * Gson
 *
 * @author hhh
 * @date 2016/9/10
 */
@SuppressWarnings("unused")
public class GsonUtil {

    public static <T> T jsonToBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Logg.sysOut("jsonString: " + jsonString);
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);

        } catch (Exception e) {
            Logg.e("解析发生异常：" + e.getMessage());
            CrashHandler.addCrash(jsonString,e);
        }
        return t;
    }

    public static <T> T jsonToBean(String jsonString, Type typeOfT) {
        T t = null;
        try {
            Logg.sysOut("jsonString: " + jsonString);
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, typeOfT);

        } catch (Throwable e) {
            Logg.e("解析发生异常：" + e.getMessage());
            CrashHandler.addCrash(jsonString,e);
        }
        return t;
    }

}
