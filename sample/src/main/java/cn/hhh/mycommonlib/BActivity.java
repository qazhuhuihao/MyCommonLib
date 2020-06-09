package cn.hhh.mycommonlib;

import android.os.Bundle;
import android.widget.TextView;

import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.rx.RxBus;
import cn.hhh.commonlib.utils.Logg;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author hhh
 */
public class BActivity extends CommonBaseActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findView(R.id.tv);

        operateBus();
    }

    /**
     * RxBus
     */
    private void operateBus() {
        RxBus.getDefault().toObservable(Integer.class)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logg.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer i) {
                        Logg.i(TAG, "onNext:" + i);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logg.e(TAG, "", e);
                    }

                    @Override
                    public void onComplete() {
                        Logg.i(TAG, "onComplete");
                    }

                });
    }
}
