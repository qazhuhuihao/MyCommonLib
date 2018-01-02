package cn.hhh.commonlib.swlog.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cn.hhh.commonlib.base.CommonBaseActivity;
import cn.hhh.commonlib.swlog.R;
import cn.hhh.commonlib.swlog.adapter.CrashFileAdapter;
import cn.hhh.commonlib.utils.FileStorageUtil;

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

    private int viewType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreate = true;
        setContentView(R.layout.debug_activity_crash);

        rvNames = findView(R.id.rv_names);
        svBody = findView(R.id.sv_body);
        tvBody = findView(R.id.tv_body);

        rvNames.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CrashFileAdapter(this, FileStorageUtil.getLogDir().listFiles(), onClickListener);
        rvNames.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (1 == viewType) {
            viewType = 0;
            rvNames.setVisibility(View.VISIBLE);
            svBody.setVisibility(View.GONE);
            return true;
        }
        isCreate = false;
        return super.onKeyDown(keyCode, event);
    }

    private String read(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
                tvBody.setText(read(file));

            }
        }
    };
}

