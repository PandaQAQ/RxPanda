package com.pandaq.rxpanda.requests.retrofit;

import android.text.TextUtils;

import com.pandaq.rxpanda.R;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.interceptor.ParamsInterceptor;
import com.pandaq.rxpanda.requests.Request;
import com.pandaq.rxpanda.ssl.SSLManager;
import com.pandaq.rxpanda.utils.CastUtils;

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
    private ParamsInterceptor paramsInterceptor;

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
            getGlobalConfig().getClientBuilder().hostnameVerifier(new SSLManager.SafeHostnameVerifier(baseUrl));
            // 添加日志拦截器
            if (getGlobalConfig().getLoggingInterceptor() != null) {
                if (!getGlobalConfig().getClientBuilder().networkInterceptors().contains(RxPanda.globalConfig().getLoggingInterceptor())) {
                    if (RxPanda.globalConfig().getLoggingInterceptor().isNetInterceptor()) {
                        getGlobalConfig().getClientBuilder().addNetworkInterceptor(RxPanda.globalConfig().getLoggingInterceptor());
                    } else {
                        getGlobalConfig().getClientBuilder().addInterceptor(RxPanda.globalConfig().getLoggingInterceptor());
                    }
                }
            }
            // 添加模拟数据
            newRetrofitBuilder.client(getGlobalConfig().getClientBuilder().build());
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
        if (paramsInterceptor == null) {
            paramsInterceptor = new ParamsInterceptor(localParams);
        } else {
            paramsInterceptor.setParamsMap(localParams);
        }
        // 将参数添加到请求中
        getGlobalConfig().getClientBuilder().addNetworkInterceptor(paramsInterceptor);
    }

    /**
     * 添加请求参数
     *
     * @param paramKey
     * @param paramValue
     * @return
     */
    public R addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.localParams.put(paramKey, paramValue);
        }
        return CastUtils.cast(this);
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    public R addParams(Map<String, String> params) {
        if (params != null) {
            this.localParams.putAll(params);
        }
        return CastUtils.cast(this);
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    public R removeParam(String paramKey) {
        if (paramKey != null) {
            this.localParams.remove(paramKey);
        }
        return CastUtils.cast(this);
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public R params(Map<String, String> params) {
        if (params != null) {
            this.localParams = params;
        }
        return CastUtils.cast(this);
    }
}
