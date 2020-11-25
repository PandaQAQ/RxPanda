package com.pandaq.rxpanda.config;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.converter.PandaConvertFactory;
import com.pandaq.rxpanda.entity.ApiData;
import com.pandaq.rxpanda.entity.IApiData;
import com.pandaq.rxpanda.entity.NullDataValue;
import com.pandaq.rxpanda.log.HttpLoggingInterceptor;
import com.pandaq.rxpanda.ssl.SSLManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import io.reactivex.annotations.NonNull;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :http global config
 */
public class HttpGlobalConfig {

    private List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();//Call适配器工厂
    private Converter.Factory converterFactory = PandaConvertFactory.create();//转换工厂,默认为 PandaConvertFactory
    private Call.Factory callFactory;//Call工厂
    private SSLSocketFactory sslSocketFactory;//SSL工厂
    private HostnameVerifier hostnameVerifier;//主机域名验证
    private ConnectionPool connectionPool;//连接池
    private Map<String, String> globalHeaders = new LinkedHashMap<>();//请求头
    private Map<String, String> globalParams = new LinkedHashMap<>();//请求参数
    private String baseUrl;//基础域名
    private long retryDelayMillis;//请求失败重试间隔时间
    private int retryCount;//请求失败重试次数
    private static HttpGlobalConfig sHttpGlobalConfig;
    private boolean isDebug;
    private Long apiSuccessCode = -1L;
    // 不验证 host 允许所有的 host
    private boolean trustAll = false;
    // Gson 解析补空默认值
    private NullDataValue defValues = new NullDataValue();
    private Class apiDataClazz = ApiData.class;
    private HttpLoggingInterceptor loggingInterceptor;

    private HttpGlobalConfig() {

    }

    public static HttpGlobalConfig getInstance() {
        if (sHttpGlobalConfig == null) {
            synchronized (HttpGlobalConfig.class) {
                if (sHttpGlobalConfig == null) {
                    sHttpGlobalConfig = new HttpGlobalConfig();
                }
            }
        }
        return sHttpGlobalConfig;
    }

    /**
     * add a CallAdapter.Factory,if never add ,will add a RxJava2CallAdapterFactory as default
     *
     * @param factory the factory to add
     * @return Config self
     */
    public HttpGlobalConfig addCallAdapterFactory(@NonNull CallAdapter.Factory factory) {
        this.callAdapterFactories.add(factory);
        return this;
    }

    /**
     * add a Converter.Factory,if never add ,will add a PandaConvertFactory as default
     *
     * @param factory the factory to add
     * @return Config self
     */
    public HttpGlobalConfig converterFactory(@NonNull Converter.Factory factory) {
        this.converterFactory = factory;
        return this;
    }

    /**
     * @param factory the factory to add
     * @return Config self
     */
    public HttpGlobalConfig callFactory(@NonNull Call.Factory factory) {
        this.callFactory = factory;
        return this;
    }

    /**
     * @param factory the factory to add
     * @return Config self
     */
    public HttpGlobalConfig sslFactory(@NonNull SSLSocketFactory factory) {
        this.sslSocketFactory = factory;
        return this;
    }

    /**
     * add a HostnameVerifier,the Request will add your baseUrl as default,if you want add other host
     * call this method
     *
     * @param verifier the hostname verifier
     * @return Config self
     */
    public HttpGlobalConfig hostVerifier(@NonNull HostnameVerifier verifier) {
        this.hostnameVerifier = verifier;
        return this;
    }

    /**
     * 添加安全认证的 hosts
     *
     * @param hosts hosts 地址
     * @return config self
     */
    public HttpGlobalConfig hosts(String... hosts) {
        // 默认添加基础域名
        if (this.hostnameVerifier == null) {
            this.hostnameVerifier = new SSLManager.SafeHostnameVerifier(hosts);
            ((SSLManager.SafeHostnameVerifier) this.hostnameVerifier).addHost(baseUrl);
        } else {
            if (this.hostnameVerifier instanceof SSLManager.SafeHostnameVerifier) {
                ((SSLManager.SafeHostnameVerifier) this.hostnameVerifier).addHosts(Arrays.asList(hosts));
                ((SSLManager.SafeHostnameVerifier) this.hostnameVerifier).addHost(baseUrl);
            } else {
                throw new IllegalArgumentException("please verifier host in your custom hostnameVerifier,or do not call hostVerifier()");
            }
        }
        return this;
    }

    /**
     * set custom connectionPool
     *
     * @param pool custom pool
     * @return Config self
     */
    public HttpGlobalConfig connectionPool(@NonNull ConnectionPool pool) {
        this.connectionPool = pool;
        return this;
    }

    /**
     * add globalHeader this header will be added with every request
     *
     * @param key    header name
     * @param header header value
     * @return Config self
     */
    public HttpGlobalConfig addGlobalHeader(@NonNull String key, String header) {
        this.globalHeaders.put(key, header);
        return this;
    }

    /**
     * add globalHeader by map
     *
     * @param headers http request headers
     * @return Config self
     */
    public HttpGlobalConfig globalHeader(@NonNull Map<String, String> headers) {
        this.globalHeaders.clear();
        this.globalHeaders.putAll(headers);
        return this;
    }

    /**
     * add localParams,the params will be added with every HttpRequest exclude RetrofitRequest
     *
     * @param params the params
     * @return config self
     */
    public HttpGlobalConfig globalParams(@NonNull Map<String, String> params) {
        this.globalParams.clear();
        this.globalParams.putAll(params);
        return this;
    }

    /**
     * add globalParam,the param will be added with every HttpRequest exclude RetrofitRequest
     *
     * @param key   the paramsKey
     * @param param the paramValue
     * @return config self
     */
    public HttpGlobalConfig addGlobalParam(@NonNull String key, String param) {
        this.globalParams.put(key, param);
        return this;
    }

    /**
     * 配置 Gson 解析的默认值
     *
     * @param defValues 默认值对象
     * @return 默认值
     */
    public HttpGlobalConfig defaultValue(@NonNull NullDataValue defValues) {
        this.defValues = defValues;
        return this;
    }

    /**
     * if you use this http lib,must call it
     *
     * @param baseUrl RetrofitRequest's baseUrl,and this url will be added to HostnameVerifier
     * @return config self
     */
    public HttpGlobalConfig baseUrl(@NonNull String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * if you open retry strategy,you can set delay between tow requests
     *
     * @param retryDelay delay time (unit is 'ms')
     * @return config self
     */
    public HttpGlobalConfig retryDelayMillis(long retryDelay) {
        this.retryDelayMillis = retryDelay;
        return this;
    }

    /**
     * set retry count
     *
     * @param retryCount retryCount
     * @return config self
     */
    public HttpGlobalConfig retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public HttpGlobalConfig interceptor(@NonNull Interceptor interceptor) {
        // 日志拦截器最后添加（避免其他拦截器添加的数据打印缺失）
        if (interceptor instanceof HttpLoggingInterceptor) {
            loggingInterceptor = (HttpLoggingInterceptor) interceptor;
        } else {
            RxPanda.getOkHttpBuilder().addInterceptor(interceptor);
        }
        return this;
    }

    public HttpGlobalConfig netInterceptor(@NonNull Interceptor netInterceptor) {
        // 日志拦截器最后添加（避免其他拦截器添加的数据打印缺失）
        if (netInterceptor instanceof HttpLoggingInterceptor) {
            loggingInterceptor = (HttpLoggingInterceptor) netInterceptor;
        } else {
            RxPanda.getOkHttpBuilder().addNetworkInterceptor(netInterceptor);
        }
        return this;
    }

    public HttpGlobalConfig readTimeout(long readTimeout) {
        RxPanda.getOkHttpBuilder().readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public HttpGlobalConfig writeTimeout(long writeTimeout) {
        RxPanda.getOkHttpBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public HttpGlobalConfig connectTimeout(long connectTimeout) {
        RxPanda.getOkHttpBuilder().connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public HttpGlobalConfig apiDataClazz(Class<? extends IApiData> clazz) {
        apiDataClazz = clazz;
        return this;
    }

    public boolean isTrustAllHost() {
        return trustAll;
    }

    /**
     * 配置是否允许所有 host
     *
     * @param trustAll 是否允许所有host
     * @return config
     */
    public HttpGlobalConfig trustAllHost(boolean trustAll) {
        this.trustAll = trustAll;
        return this;
    }

    //    ######################################## getter ########################################

    public List<CallAdapter.Factory> getCallAdapterFactories() {
        return callAdapterFactories;
    }

    public Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    public Call.Factory getCallFactory() {
        return callFactory;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public Map<String, String> getGlobalHeaders() {
        return globalHeaders;
    }

    public Map<String, String> getGlobalParams() {
        return globalParams;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public NullDataValue getDefValues() {
        return defValues;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public HttpGlobalConfig debug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public Class getApiDataClazz() {
        return apiDataClazz;
    }

    public Long getApiSuccessCode() {
        return apiSuccessCode;
    }

    public HttpGlobalConfig apiSuccessCode(Long apiSuccessCode) {
        this.apiSuccessCode = apiSuccessCode;
        return this;
    }

    public HttpLoggingInterceptor getLoggingInterceptor() {
        return loggingInterceptor;
    }
}
