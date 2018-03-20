package cn.hhh.commonlib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.hhh.commonlib.utils.Logg;


/**
 * function : Fragment基类(兼容低版本).
 * <p></p>
 *
 * @author lzj
 * @date 2015/12/31
 */
@SuppressWarnings({"unused", "deprecation"})
public class CommonBaseFragment extends Fragment {
    /** 日志输出标志 **/
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        Logg.d(TAG, "-->onAttach(context:" + context.toString() + ")");
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logg.d(TAG, "-->onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logg.d(TAG, "-->onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Logg.d(TAG, "-->onResume()");
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logg.d(TAG, "-->onActivityResult(requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + (data == null ? "null" : data.toString()) + ")");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        Logg.d(TAG, "-->onStop()");
        super.onStop();
    }

    @Override
    public void onPause() {
        Logg.d(TAG, "-->onPause()");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Logg.d(TAG, "-->onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Logg.d(TAG, "-->onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logg.d(TAG, "-->onDetach()");
        super.onDetach();
    }


    protected final <T extends View> T findView(View view, int id) {
        return view.findViewById(id);
    }
}
