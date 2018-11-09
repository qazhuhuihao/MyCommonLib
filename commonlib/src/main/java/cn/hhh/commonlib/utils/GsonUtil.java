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

    private static Gson gson;

    public static Gson getGson() {
        if (null == gson)
            gson = new Gson();

        return gson;
    }

    public static <T> T jsonToBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Logg.i("jsonString: ", jsonString);

            t = getGson().fromJson(jsonString, cls);

        } catch (Exception e) {
            Logg.e("解析发生异常：" + e.getMessage());
        }
        return t;
    }

    public static <T> T jsonToBean(String jsonString, Type typeOfT) {
        T t = null;
        try {
            Logg.i("jsonString: ", jsonString);
            t = getGson().fromJson(jsonString, typeOfT);

        } catch (Throwable e) {
            Logg.e("解析发生异常：" + e.getMessage());
        }
        return t;
    }

    public static <T> String beanToJson(T bean) {
        String json = null;

        try {
            json = getGson().toJson(bean);
            Logg.i("jsonString: ", json);
        } catch (Exception e) {
            Logg.e("解析发生异常：" + e.getMessage());
        }
        return json;
    }

}
