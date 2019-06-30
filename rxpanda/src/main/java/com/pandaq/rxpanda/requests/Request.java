package com.pandaq.rxpanda.requests;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.config.CONFIG;
import com.pandaq.rxpanda.config.HttpGlobalConfig;
import com.pandaq.rxpanda.converter.PandaConvertFactory;
import com.pandaq.rxpanda.interceptor.HeaderInterceptor;
import com.pandaq.rxpanda.ssl.SSLManager;
import com.pandaq.rxpanda.utils.CastUtils;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :base http request
 */
public class Request<T extends Request> {

    // local basUrl
    private String baseUrl = "";
    // local readTimeout
    private Long readTimeout = 0L;
    // local writeTimeout
    private Long writeTimeout = 0L;
    // local connectTimeout
    private Long connectTimeout = 0L;

    private Map<String, String> headers = new LinkedHashMap<>();
    private List<Interceptor> interceptors = new ArrayList<>();
    private List<Interceptor> networkInterceptors = new ArrayList<>();

    // default global config
    protected HttpGlobalConfig mGlobalConfig;

    protected Retrofit retrofit;

    /**
     * set request base url
     *
     * @param baseUrl base url
     * @return request
     */
    public T baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return CastUtils.cast(this);
    }

    /**
     * 添加 header map
     *
     * @param headers 请求头 map
     * @return 请求体
     */
    public T addHeaders(@NonNull Map<String, String> headers) {
        this.headers.putAll(headers);
        return CastUtils.cast(this);
    }

    /**
     * 添加 header map
     *
     * @param key   请求头 key
     * @param value 请求头 value
     * @return 请求体
     */
    public T addHeader(@NonNull String key, String value) {
        this.headers.put(key, value);
        return CastUtils.cast(this);
    }

    /**
     * 设置 header map，会覆盖之前的 header
     *
     * @param headers 请求头 map
     * @return 请求体
     */
    public T resetHeader(@NonNull Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
        return CastUtils.cast(this);
    }

    /**
     * 移除指定 header
     *
     * @param key 请求头 key
     * @return 请求体
     */
    public T removeHeader(String key) {
        this.headers.remove(key);
        return CastUtils.cast(this);
    }

    /**
     * 移除全部 header
     *
     * @return 请求体
     */
    public T cleanHeader() {
        this.headers.clear();
        return CastUtils.cast(this);
    }

//    ##################### interceptors ####################

    public T interceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        return CastUtils.cast(this);
    }

    public T netInterceptor(Interceptor interceptor) {
        networkInterceptors.add(interceptor);
        return CastUtils.cast(this);
    }

    public T clearInterceptor() {
        interceptors.clear();
        return CastUtils.cast(this);
    }

    public T clearNetInterceptor() {
        networkInterceptors.clear();
        return CastUtils.cast(this);
    }


    public T readTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return CastUtils.cast(this);
    }

    public T writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return CastUtils.cast(this);
    }

    public T connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return CastUtils.cast(this);
    }

    /**
     * 使用全局配置覆盖当前配置
     */
    private void resetGlobalParams() {
        mGlobalConfig = RxPanda.globalConfig();
        OkHttpClient.Builder builder = RxPanda.getOkHttpBuilder();

        // http client config
        if (mGlobalConfig.getConnectionPool() == null) {
            mGlobalConfig.connectionPool(new ConnectionPool(CONFIG.DEFAULT_MAX_IDLE_CONNECTIONS,
                    CONFIG.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS));
        }
        mGlobalConfig.connectionPool(mGlobalConfig.getConnectionPool());

        if (mGlobalConfig.getHostnameVerifier() == null) {
            mGlobalConfig.hostVerifier(new SSLManager.SafeHostnameVerifier(mGlobalConfig.getBaseUrl()));
        }
        builder.hostnameVerifier(mGlobalConfig.getHostnameVerifier());

        if (mGlobalConfig.getSslSocketFactory() == null) {
            mGlobalConfig.sslFactory(SSLManager.getSslSocketFactory(null, null, null));
        }
        builder.sslSocketFactory(mGlobalConfig.getSslSocketFactory());
        builder.connectTimeout(CONFIG.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(CONFIG.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(CONFIG.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        // retrofit config
        Retrofit.Builder retrofitBuilder = RxPanda.getRetrofitBuilder();
        if (!TextUtils.isEmpty(mGlobalConfig.getBaseUrl())) {
            retrofitBuilder.baseUrl(mGlobalConfig.getBaseUrl());
        } else {
            throw new IllegalArgumentException("base url can not be empty !!!");
        }
        if (mGlobalConfig.getConverterFactory() == null) {
            mGlobalConfig.converterFactory(PandaConvertFactory.create());
        }
        retrofitBuilder.addConverterFactory(mGlobalConfig.getConverterFactory());
        if (mGlobalConfig.getCallAdapterFactories().isEmpty()) {
            mGlobalConfig.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        for (CallAdapter.Factory factory : mGlobalConfig.getCallAdapterFactories()) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }
        if (mGlobalConfig.getCallFactory() != null) {
            retrofitBuilder.callFactory(mGlobalConfig.getCallFactory());
        }

    }

    /**
     * 注入本地配置参数
     */
    protected void injectLocalParams() {
        // 注入本地配置前先重置现有配置为全局默认配置
        resetGlobalParams();
        OkHttpClient.Builder okHttpBuilder = RxPanda.getOkHttpBuilder();

        // 添加请求头
        if (mGlobalConfig.getGlobalHeaders() != null) {
            // 全局的请求头设置进去,将本地 header 加入到全局中（本地同名覆盖全局）
            mGlobalConfig.getGlobalHeaders().putAll(headers);
        }
        if (!headers.isEmpty()) {
            okHttpBuilder.addInterceptor(new HeaderInterceptor(headers));
        }
        // 添加请求拦截器
        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }
        // 添加请求网络拦截器
        if (!networkInterceptors.isEmpty()) {
            for (Interceptor interceptor : networkInterceptors) {
                okHttpBuilder.addInterceptor(interceptor);
            }
        }
        //设置局部超时时间和重试次数
        if (readTimeout > 0) {
            okHttpBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        if (writeTimeout > 0) {
            okHttpBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }
        if (connectTimeout > 0) {
            okHttpBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }
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
            okHttpBuilder.hostnameVerifier(new SSLManager.SafeHostnameVerifier(baseUrl));
            newRetrofitBuilder.client(okHttpBuilder.build());
            retrofit = newRetrofitBuilder.build();
        } else { // 使用默认配置的对象
            RxPanda.getRetrofitBuilder().client(okHttpBuilder.build());
            retrofit = RxPanda.getRetrofitBuilder().build();
        }
    }

    /**
     * 获取第一级type
     *
     * @param k   类对象
     * @param <K> 泛型
     * @return 泛型 Type
     */
    protected <K> Type getType(K k) {
        Type genType = k.getClass().getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }
}
