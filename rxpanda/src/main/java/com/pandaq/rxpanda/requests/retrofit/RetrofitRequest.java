package com.pandaq.rxpanda.requests.retrofit;

import android.text.TextUtils;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.requests.Request;
import com.pandaq.rxpanda.ssl.SSLManager;
import com.pandaq.rxpanda.utils.CastUtils;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 * <p>
 * Description : request for use retrofit
 */
public class RetrofitRequest extends Request<RetrofitRequest> {

    // local basUrl
    private String baseUrl = "";

    public RetrofitRequest() {
    }

    /**
     * set request base url
     *
     * @param baseUrl base url
     * @return request
     */
    public RetrofitRequest baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public <T> T create(Class<T> apiService) {
        injectLocalParams();
        if (!TextUtils.isEmpty(baseUrl)) { // 如果基础地址改了者需要重新构建个 Retrofit 对象，避免影响默认请求的配置
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            if (mGlobalConfig.getConverterFactory() != null) {
                newRetrofitBuilder.addConverterFactory(mGlobalConfig.getConverterFactory());
            }
            if (!mGlobalConfig.getCallAdapterFactories().isEmpty()) {
                for (CallAdapter.Factory factory : mGlobalConfig.getCallAdapterFactories()) {
                    newRetrofitBuilder.addCallAdapterFactory(factory);
                }
            }
            if (mGlobalConfig.getCallFactory() != null) {
                newRetrofitBuilder.callFactory(mGlobalConfig.getCallFactory());
            }
            builder.hostnameVerifier(new SSLManager.SafeHostnameVerifier(baseUrl));
            newRetrofitBuilder.client(builder.build());
            retrofit = newRetrofitBuilder.build();
        } else { // 使用默认配置的对象
            RxPanda.getRetrofitBuilder().client(builder.build());
            retrofit = RxPanda.getRetrofitBuilder().build();
        }
        return retrofit.create(apiService);
    }
}
