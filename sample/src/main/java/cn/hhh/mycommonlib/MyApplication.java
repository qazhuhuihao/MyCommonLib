package cn.hhh.mycommonlib;

import cn.hhh.commonlib.MyBaseApplication;
import cn.hhh.commonlib.common.Configs;
import cn.hhh.commonlib.xlog.XLogInit;

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

        XLogInit.init(this);
    }

}
