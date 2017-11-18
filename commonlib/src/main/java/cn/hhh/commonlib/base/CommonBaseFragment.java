package cn.hhh.commonlib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.hhh.commonlib.utils.Logg;


/**
 * function : Fragment基类(兼容低版本).
 * <p></p>
 * Created by lzj on 2015/12/31.
 */
@SuppressWarnings({"unused", "deprecation"})
public class CommonBaseFragment extends Fragment {
    /** 日志输出标志 **/
    protected final String TAG = this.getClass().getSimpleName();
    /** fragment根布局 */
    //protected View mRootView;

    @Override
    public void onAttach(Context context) {
        Logg.d(TAG, TAG + "-->onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logg.d(TAG, TAG + "-->onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logg.d(TAG, TAG + "-->onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Logg.d(TAG, TAG + "-->onResume()");
        super.onResume();
    }

    @Override
    public void onStop() {
        Logg.d(TAG, TAG + "-->onStop()");
        super.onStop();
    }

    @Override
    public void onPause() {
        Logg.d(TAG, TAG + "-->onPause()");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Logg.d(TAG, TAG + "-->onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Logg.d(TAG, TAG + "-->onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logg.d(TAG, TAG + "-->onDetach()");
        super.onDetach();
    }

//    @SuppressWarnings("unchecked")
//    protected final <T extends View> T findView(int id) {
//        return (T) mRootView.findViewById(id);
//    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }
}
