package cn.hhh.commonlib.swlog.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.imp.FileNameSelector;
import cn.hhh.commonlib.swlog.R;
import cn.hhh.commonlib.swlog.adapter.CrashFileAdapter;
import cn.hhh.commonlib.utils.FileStorageUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author qazhu
 * @date 2018/1/2
 */

public class CrashActivity extends CommonBaseActivity {

    public static boolean isCreate;

    private RecyclerView rvNames;
    private ScrollView svBody;
    private TextView tvBody;

    private CrashFileAdapter adapter;
    private Disposable disposable;

    private int viewType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreate = true;
        setContentView(R.layout.debug_activity_crash);

        rvNames = findView(R.id.rv_names);
        svBody = findView(R.id.sv_body);
        tvBody = findView(R.id.tv_body);

        rvNames.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new CrashFileAdapter(FileStorageUtil.getLogDir().listFiles(new FileNameSelector("txt", "log", "crashLog")), onClickListener);//new FileNameSelector("txt", "log", "crashLog")
        rvNames.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (1 == viewType) {
            viewType = 0;
            rvNames.setVisibility(View.VISIBLE);
            svBody.setVisibility(View.GONE);
            unSubscribe();
            return true;
        }
        isCreate = false;
        return super.onKeyDown(keyCode, event);
    }

    private void read(final TextView tv, final File file) {

        disposable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                fileReader.close();
                emitter.onNext(stringBuilder.toString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        tv.setText(s);
                    }
                });

//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//            bufferedReader.close();
//            fileReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        tv.setText(stringBuilder.toString());

    }

    private void unSubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.fl_item_crash_root) {
                svBody.setVisibility(View.VISIBLE);
                rvNames.setVisibility(View.GONE);
                tvBody.setText("读取中...");
                viewType = 1;
                File file = (File) v.getTag();
                read(tvBody, file);

            }
        }
    };
}

