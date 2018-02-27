package cn.hhh.mycommonlib;

import cn.hhh.commonlib.MyBaseApplication;
import cn.hhh.commonlib.common.Configs;
import cn.hhh.commonlib.utils.Logg;

/**
 * @author qazhu
 * @date 2017/12/11
 */

public class MyApplication extends MyBaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //栗子默认使用debug模式
        Configs.DEBUG = true;

        if (Configs.DEBUG) {
            Logg.LoggExample loggExample = new Logg.LoggExample();
            boolean flag = false;
            for (Logg.LoggInterface loggInterface : Logg.getLoggs()) {
                if (loggInterface.getTAG().equals(loggExample.getTAG())) {
                    flag = true;
                    break;
                }
            }
            if (!flag)
                Logg.addLogg(loggExample);
        }
    }

}
