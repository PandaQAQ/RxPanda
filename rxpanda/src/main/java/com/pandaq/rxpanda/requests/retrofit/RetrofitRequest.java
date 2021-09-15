package com.pandaq.rxpanda.requests.retrofit;

import android.text.TextUtils;

import com.pandaq.rxpanda.interceptor.MockDataInterceptor;
import com.pandaq.rxpanda.interceptor.ParamsInterceptor;
import com.pandaq.rxpanda.requests.Request;
import com.pandaq.rxpanda.ssl.SSLManager;

import java.util.LinkedHashMap;
import java.util.Map;

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
    protected Map<String, String> localParams = new LinkedHashMap<>();//请求参数

    public RetrofitRequest() {
        super();
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
        Retrofit retrofit;
        injectLocalParams();
        if (!TextUtils.isEmpty(baseUrl)) { // 如果基础地址改了者需要重新构建个 Retrofit 对象，避免影响默认请求的配置
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            if (getGlobalConfig().getConverterFactory() != null) {
                newRetrofitBuilder.addConverterFactory(getGlobalConfig().getConverterFactory());
            }
            if (!getGlobalConfig().getCallAdapterFactories().isEmpty()) {
                for (CallAdapter.Factory factory : getGlobalConfig().getCallAdapterFactories()) {
                    newRetrofitBuilder.addCallAdapterFactory(factory);
                }
            }
            getClientBuilder().hostnameVerifier(new SSLManager.SafeHostnameVerifier(baseUrl));
            // 添加日志拦截器
            if (getGlobalConfig().getLoggingInterceptor() != null) {
                if (getGlobalConfig().getLoggingInterceptor().isNetInterceptor()) {
                    getClientBuilder().addNetworkInterceptor(getGlobalConfig().getLoggingInterceptor());
                } else {
                    getClientBuilder().addInterceptor(getGlobalConfig().getLoggingInterceptor());
                }
            }
            // 添加调试阶段的模拟数据拦截器
            if (getGlobalConfig().isDebug()) {
                MockDataInterceptor dataInterceptor = getGlobalConfig().getMockDataInterceptor();
                dataInterceptor.setLocalMockJson(getMockJson());
                getClientBuilder().addNetworkInterceptor(dataInterceptor);
            }
            newRetrofitBuilder.client(getGlobalConfig().getClientBuilder().build().newBuilder().build());
            retrofit = newRetrofitBuilder.build();
        } else { // 使用默认配置的对象
            retrofit = getCommonRetrofit();
        }
        return retrofit.create(apiService);
    }

    @Override
    protected void injectLocalParams() {
        super.injectLocalParams();
        // retrofit 方式请求，通过拦截器添加参数
        if (getGlobalConfig().getGlobalParams() != null) {
            localParams.putAll(getGlobalConfig().getGlobalParams());
        }

        if (!localParams.isEmpty()) {
            ParamsInterceptor paramsInterceptor = getGlobalConfig().getParamsInterceptor();
            paramsInterceptor.setParamsMap(localParams);
            if (!getClientBuilder().networkInterceptors().contains(paramsInterceptor)) {
                // 将参数添加到请求中
                getClientBuilder().addNetworkInterceptor(paramsInterceptor);
            }
        }
    }

    /**
     * 添加请求参数
     *
     * @param paramKey
     * @param paramValue
     * @return
     */
    public RetrofitRequest addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.localParams.put(paramKey, paramValue);
        }
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    public RetrofitRequest addParams(Map<String, String> params) {
        if (params != null) {
            this.localParams.putAll(params);
        }
        return this;
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    public RetrofitRequest removeParam(String paramKey) {
        if (paramKey != null) {
            this.localParams.remove(paramKey);
        }
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public RetrofitRequest params(Map<String, String> params) {
        if (params != null) {
            this.localParams = params;
        }
        return this;
    }
}
