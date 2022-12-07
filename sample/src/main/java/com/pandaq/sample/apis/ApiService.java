package com.pandaq.sample.apis;

import com.pandaq.rxpanda.annotation.MockJson;
import com.pandaq.rxpanda.annotation.Timeout;
import com.pandaq.sample.Constants;
import com.pandaq.sample.entities.User;
import com.pandaq.sample.entities.UserTest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by huxinyu on 2018/4/2.
 * Email : panda.h@foxmail.com
 * Description : retrofit api 接口
 */
public interface ApiService {

    //    @MockJson(json = Constants.MOCK_STRING)
    @GET("https://www.baidu.com")
    Observable<Object> stringData();

    // 与 ApiData 结构完全不一样使用 RealEntity 标准不做脱壳处理，返回 ZhihuData 就解析为 ZhihuData
    @MockJson(json = Constants.MOCK_USER)
    @GET("https://news-at.zhihu.com/api/4/news/latest")
    Observable<User> getUser();

    @MockJson(json = Constants.MOCK_INT)
    @GET("https://www.google.com")
    @Timeout(connectTimeout = 5000,readTimeout = 5000,writeTimeout = 5000)
    Observable<Integer> intData();


//    @MockJson(json = Constants.MOCK_TYPE_ERROR)
//    @GET("https://www.baidu.com")
//    Observable<User> typeError();

    @MockJson(json = Constants.MOCK_DATA)
    @GET("https://www.baidu.com")
    Observable<List<UserTest>> typeError(@Query("test") String test);
}
