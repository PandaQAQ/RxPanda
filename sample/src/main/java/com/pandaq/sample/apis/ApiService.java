package com.pandaq.sample.apis;

import com.pandaq.app_launcher.entites.ZhihuData;
import com.pandaq.rxpanda.entity.EmptyData;
import com.pandaq.sample.entities.apidata.ZooApiData;
import com.pandaq.rxpanda.annotation.ApiData;
import com.pandaq.rxpanda.annotation.RealEntity;
import com.pandaq.sample.entities.ZooData;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

/**
 * Created by huxinyu on 2018/4/2.
 * Email : panda.h@foxmail.com
 * Description : retrofit api 接口
 */
public interface ApiService {

    // 在线 mock 正常使用 ApiData 数据壳
    @GET("zooData")
    Observable<List<ZooData>> getZooList();

    // 数据结构不变但是数据壳 jsonKey 与框架默认不一致时使用此注解，也可在 Config 配置全局使用此数据壳
    @ApiData(clazz = ZooApiData.class)
    @GET("https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/customApiData")
    Observable<List<ZooData>> newJsonKeyData();

    @GET("https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/boolean")
    Observable<Boolean> stringData();

    // 与 ApiData 结构完全不一样使用 RealEntity 标准不做脱壳处理，返回 ZhihuData 就解析为 ZhihuData
    @RealEntity
    @GET("https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/zhihuData")
    Observable<ZhihuData> zhihu();
}
