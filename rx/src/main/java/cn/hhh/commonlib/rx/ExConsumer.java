package cn.hhh.commonlib.rx;

import cn.hhh.commonlib.CrashHandler;
import io.reactivex.functions.Consumer;

/**
 *
 * @author hhh
 * @date 2017/11/25
 */
@SuppressWarnings("unused")
public class ExConsumer implements Consumer<Exception> {
    @Override
    public void accept(Exception e) throws Exception {
        CrashHandler.addCrash("", e);
    }
}
