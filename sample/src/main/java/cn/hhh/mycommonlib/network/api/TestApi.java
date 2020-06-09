package cn.hhh.mycommonlib.network.api;


import cn.hhh.mycommonlib.bean.BaseBean;
import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 *
 * @author qazhu
 * @date 2017/11/23
 */

public interface TestApi {

    @POST("rest/test")
    Observable<BaseBean> test();


}
