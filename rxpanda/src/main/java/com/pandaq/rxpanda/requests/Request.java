package com.pandaq.rxpanda.requests;

import android.text.TextUtils;

import com.pandaq.rxpanda.config.CONFIG;
import com.pandaq.rxpanda.config.HttpGlobalConfig;
import com.pandaq.rxpanda.converter.PandaConvertFactory;
import com.pandaq.rxpanda.interceptor.HeaderInterceptor;
import com.pandaq.rxpanda.interceptor.MockDataInterceptor;
import com.pandaq.rxpanda.ssl.SSLManager;
import com.pandaq.rxpanda.utils.CastUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :base http request
 */
public class Request<T extends Request<T>> {
    // local readTimeout
    private Long readTimeout = 0L;
    // local writeTimeout
    private Long writeTimeout = 0L;
    // local connectTimeout
    private Long connectTimeout = 0L;

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<Interceptor> interceptors = new ArrayList<>();
    private final List<Interceptor> networkInterceptors = new ArrayList<>();
    protected OkHttpClient.Builder clientBuilder = null;

    public Request() {
        clientBuilder = null;
    }

    /**
     * 获取子类指定的模拟数据，默认为空
     */
    protected String getMockJson() {
        return null;
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
        // http client config
        if (getGlobalConfig().getConnectionPool() == null) {
            getGlobalConfig().connectionPool(new ConnectionPool(CONFIG.DEFAULT_MAX_IDLE_CONNECTIONS,
                    CONFIG.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS));
        }
        getGlobalConfig().connectionPool(getGlobalConfig().getConnectionPool());

        if (getGlobalConfig().getHostnameVerifier() == null) {
            getGlobalConfig().hostVerifier(new SSLManager.SafeHostnameVerifier(getGlobalConfig().getBaseUrl()));
        }
        getClientBuilder().hostnameVerifier(getGlobalConfig().getHostnameVerifier());

        if (getGlobalConfig().getSslSocketFactory() == null) {
            getGlobalConfig().sslFactory(SSLManager.getSslSocketFactory(null, null, null));
        }
        getClientBuilder().sslSocketFactory(getGlobalConfig().getSslSocketFactory());
        getClientBuilder().connectTimeout(getGlobalConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);
        getClientBuilder().readTimeout(getGlobalConfig().getReadTimeout(), TimeUnit.MILLISECONDS);
        getClientBuilder().writeTimeout(getGlobalConfig().getWriteTimeout(), TimeUnit.MILLISECONDS);
        // 添加全局的拦截器
        for (Interceptor interceptor : getGlobalConfig().getInterceptors()) {
            if (!getClientBuilder().interceptors().contains(interceptor)) {
                getClientBuilder().addInterceptor(interceptor);
            }
        }
        for (Interceptor interceptor : getGlobalConfig().getNetInterceptors()) {
            if (!getClientBuilder().networkInterceptors().contains(interceptor)) {
                getClientBuilder().addNetworkInterceptor(interceptor);
            }
        }
        getClientBuilder().retryOnConnectionFailure(true);
    }

    /**
     * å
     * 注入本地配置参数
     */
    protected void injectLocalParams() {
        // 注入本地配置前先重置现有配置为全局默认配置
        resetGlobalParams();

        // 添加请求头
        if (getGlobalConfig().getGlobalHeaders() != null) {
            // 全局的请求头设置进去,将全局加入到本地 header 中（本地同名覆盖全局）
            headers.putAll(getGlobalConfig().getGlobalHeaders());
        }
        if (!headers.isEmpty()) {
            getClientBuilder().addInterceptor(new HeaderInterceptor(headers));
        }
        // 添加请求拦截器
        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (!getClientBuilder().interceptors().contains(interceptor)) {
                    getClientBuilder().addInterceptor(interceptor);
                }
            }
        }
        // 添加请求网络拦截器
        if (!networkInterceptors.isEmpty()) {
            for (Interceptor interceptor : networkInterceptors) {
                if (!getClientBuilder().networkInterceptors().contains(interceptor)) {
                    getClientBuilder().addInterceptor(interceptor);
                }
            }
        }
        //设置局部超时时间和重试次数
        if (readTimeout > 0) {
            getClientBuilder().readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        if (writeTimeout > 0) {
            getClientBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }
        if (connectTimeout > 0) {
            getClientBuilder().connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
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

    /**
     * 获取全局的通用配置 Retrofit 对象
     *
     * @return retrofit
     */
    protected Retrofit getCommonRetrofit() {
        // retrofit config
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        if (!TextUtils.isEmpty(getGlobalConfig().getBaseUrl())) {
            retrofitBuilder.baseUrl(getGlobalConfig().getBaseUrl());
        } else {
            throw new IllegalArgumentException("base url can not be empty !!!");
        }
        if (getGlobalConfig().getConverterFactory() == null) {
            getGlobalConfig().converterFactory(PandaConvertFactory.create());
        }
        retrofitBuilder.addConverterFactory(getGlobalConfig().getConverterFactory());
        if (getGlobalConfig().getCallAdapterFactories().isEmpty()) {
            getGlobalConfig().addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        }
        for (CallAdapter.Factory factory : getGlobalConfig().getCallAdapterFactories()) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }
        // 添加日志拦截器
        if (getGlobalConfig().getLoggingInterceptor() != null) {
            if (getGlobalConfig().getLoggingInterceptor().isNetInterceptor()) {
                if (!getClientBuilder().networkInterceptors().contains(getGlobalConfig().getLoggingInterceptor())) {
                    getClientBuilder().addNetworkInterceptor(getGlobalConfig().getLoggingInterceptor());
                }
            } else {
                if (!getClientBuilder().interceptors().contains(getGlobalConfig().getLoggingInterceptor())) {
                    getClientBuilder().addInterceptor(getGlobalConfig().getLoggingInterceptor());
                }
            }
        }
        // 添加调试阶段的模拟数据拦截器
        if (getGlobalConfig().isAlwaysUseMock() || getGlobalConfig().isDebug()) {
            MockDataInterceptor dataInterceptor = getGlobalConfig().getMockDataInterceptor();
            dataInterceptor.setLocalMockJson(getMockJson());
            if (!getClientBuilder().networkInterceptors().contains(dataInterceptor)) {
                getClientBuilder().addNetworkInterceptor(dataInterceptor);
            }
        }
        OkHttpClient client = getClientBuilder().build();
        return retrofitBuilder.client(client).build();
    }

    protected HttpGlobalConfig getGlobalConfig() {
        return HttpGlobalConfig.getInstance();
    }

    protected OkHttpClient.Builder getClientBuilder() {
        // GlobalConfig 中的 builder 只用于保存属性，request 根据其属性各自创建新的 builder
        if (clientBuilder == null) {
            clientBuilder = getGlobalConfig().getClientBuilder().build().newBuilder();
        }
        return clientBuilder;
    }
}
