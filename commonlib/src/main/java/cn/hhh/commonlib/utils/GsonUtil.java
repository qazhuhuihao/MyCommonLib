package cn.hhh.commonlib.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson
 * Created by hhh on 2016/9/10.
 */
@SuppressWarnings("unused")
public class GsonUtil {

    public static <T> T jsonToBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Logg.sysOut("jsonString: " + jsonString);
            Logg.d("NoHttp: " + jsonString);
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);

        } catch (Exception e) {
            Logg.e("解析发生异常：" + e.getMessage());
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T jsonToBean(String jsonString, Type typeOfT) {
        T t = null;
        try {
            Logg.sysOut("jsonString: " + jsonString);
            Logg.d("NoHttp: " + jsonString);
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, typeOfT);

        } catch (Throwable e) {
            e.printStackTrace();
            Logg.e("解析发生异常：" + e.getMessage());
        }
        return t;
    }

}
