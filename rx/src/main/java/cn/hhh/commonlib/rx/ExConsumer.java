package cn.hhh.commonlib.rx;

import cn.hhh.commonlib.utils.Logg;
import io.reactivex.functions.Consumer;

/**
 * @author hhh
 * @date 2017/11/25
 */
@SuppressWarnings("unused")
public class ExConsumer implements Consumer<Throwable> {
    private static final String TAG = ExConsumer.class.getSimpleName();

    @Override
    public void accept(Throwable e) throws Exception {
        //CrashHandler.addCrash("", e);
        Logg.e(TAG, "", e);
    }
}
