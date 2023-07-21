package com.pandaq.rxpanda.config

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.NonNull
import com.pandaq.rxpanda.converter.PandaConvertFactory
import com.pandaq.rxpanda.entity.ApiData
import com.pandaq.rxpanda.entity.IApiData
import com.pandaq.rxpanda.entity.NullDataValue
import com.pandaq.rxpanda.log.HttpLoggingInterceptor
import com.pandaq.rxpanda.ssl.SSLManager.SafeHostnameVerifier
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.Arrays
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :http global config
 */
class HttpGlobalConfig private constructor() {
    private var context: Context? = null
    private val interceptors: MutableList<Interceptor> = ArrayList()
    private val netInterceptors: MutableList<Interceptor> = ArrayList()
    private val callAdapterFactories: MutableList<CallAdapter.Factory> = ArrayList() //Call适配器工厂

    //转换工厂,默认为 PandaConvertFactory
    var converterFactory: Converter.Factory = PandaConvertFactory.create()

    //SSL工厂
    var sslSocketFactory: SSLSocketFactory? = null

    //主机域名验证
    var hostnameVerifier: HostnameVerifier? = null

    //连接池
    var connectionPool: ConnectionPool? = null
    private val globalHeaders: MutableMap<String, String> = LinkedHashMap() //请求头
    private val globalParams: MutableMap<String, String> = LinkedHashMap() //请求参数

    //基础域名
    var baseUrl: String = ""
    var isDebug = false
    var apiSuccessCode = "0"

    // 不验证 host 允许所有的 host
    var isTrustAllHost = false

    // Gson 解析补空默认值
    var defValues: NullDataValue? = null
    var loggingInterceptor: HttpLoggingInterceptor? = null
    private var apiDataClazz: Class<out IApiData<*>?> = ApiData::class.java
    private var clientBuilder = defaultClientBuilder

    // 简单的缓存，无网或请求失败用缓存，有网用新数据
    private var cache: Cache? = null

    // false 只有标注使用缓存的接口才使用缓存，true 除了忽略注解和 IO 请求外的所有请求都使用缓存
    private var cacheAll = false

    //请求失败重试间隔时间
    var retryDelayMillis: Long = 0

    //请求失败重试次数
    var retryCount = 0
    var connectTimeout = CONFIG.DEFAULT_TIMEOUT * 1000L //连接超时时间，默认 10 秒
    var readTimeout = CONFIG.DEFAULT_TIMEOUT * 1000L //连接超时时间，默认 10 秒
    var writeTimeout = CONFIG.DEFAULT_TIMEOUT * 1000L //连接超时时间，默认 10 秒

    //@Mock 标注或者配置了 mockJson 的接口，是否总是使用模拟数据（false 为默认值，只有 Debug 模式下才使用 mock 数据）
    var isAlwaysUseMock = false

    /**
     * set custom clientConfigs,the same config will be covered by Rxpanda
     *
     * @param clientBuilder CustomBuilder
     * @return Config self
     */
    fun client(clientBuilder: OkHttpClient.Builder): HttpGlobalConfig {
        this.clientBuilder = clientBuilder
        return this
    }

    /**
     * set Cache Strategy, if do not set, will cache nothing
     *
     * @param cache cache Strategy
     * @return Config self
     */
    fun cache(cache: Cache?, cacheAll: Boolean): HttpGlobalConfig {
        this.cache = cache
        this.cacheAll = cacheAll
        return this
    }

    /**
     * add a CallAdapter.Factory
     *
     * @param factory the factory to add
     * @return Config self
     */
    fun addCallAdapterFactory(factory: CallAdapter.Factory): HttpGlobalConfig {
        callAdapterFactories.add(factory)
        return this
    }

    /**
     * add a Converter.Factory,if never add ,will add a PandaConvertFactory as default
     *
     * @param factory the factory to add
     * @return Config self
     */
    fun converterFactory(factory: Converter.Factory): HttpGlobalConfig {
        converterFactory = factory
        return this
    }

    /**
     * @param factory the factory to add
     * @return Config self
     */
    fun sslFactory(factory: SSLSocketFactory): HttpGlobalConfig {
        sslSocketFactory = factory
        return this
    }

    /**
     * add a HostnameVerifier,the Request will add your baseUrl as default,if you want add other host
     * call this method
     *
     * @param verifier the hostname verifier
     * @return Config self
     */
    fun hostVerifier(verifier: HostnameVerifier): HttpGlobalConfig {
        hostnameVerifier = verifier
        return this
    }

    /**
     * 添加安全认证的 hosts
     *
     * @param hosts hosts 地址
     * @return config self
     */
    fun hosts(vararg hosts: String): HttpGlobalConfig {
        // 默认添加基础域名
        if (hostnameVerifier == null) {
            hostnameVerifier = SafeHostnameVerifier(*hosts)
            (hostnameVerifier as SafeHostnameVerifier?)?.addHost(baseUrl)
        } else {
            if (hostnameVerifier is SafeHostnameVerifier) {
                (hostnameVerifier as SafeHostnameVerifier?)!!.addHosts(Arrays.asList(*hosts))
                (hostnameVerifier as SafeHostnameVerifier?)!!.addHost(baseUrl)
            } else {
                throw IllegalArgumentException("please verifier host in your custom hostnameVerifier,or do not call hostVerifier()")
            }
        }
        return this
    }

    /**
     * set custom connectionPool
     *
     * @param pool custom pool
     * @return Config self
     */
    fun connectionPool(pool: ConnectionPool): HttpGlobalConfig {
        connectionPool = pool
        return this
    }

    /**
     * add globalHeader this header will be added with every request
     *
     * @param key    header name
     * @param header header value
     * @return Config self
     */
    fun addGlobalHeader(key: String, header: String): HttpGlobalConfig {
        globalHeaders[key] = header
        return this
    }

    /**
     * add globalHeader by map
     *
     * @param headers http request headers
     * @return Config self
     */
    fun globalHeader(headers: Map<String, String>): HttpGlobalConfig {
        globalHeaders.clear()
        globalHeaders.putAll(headers!!)
        return this
    }

    /**
     * add localParams,the params will be added with every HttpRequest exclude RetrofitRequest
     *
     * @param params the params
     * @return config self
     */
    fun globalParams(params: Map<String, String>): HttpGlobalConfig {
        globalParams.clear()
        globalParams.putAll(params)
        return this
    }

    /**
     * add globalParam,the param will be added with every HttpRequest exclude RetrofitRequest
     *
     * @param key   the paramsKey
     * @param param the paramValue
     * @return config self
     */
    fun addGlobalParam(key: String, param: String): HttpGlobalConfig {
        globalParams[key] = param
        return this
    }

    /**
     * 配置 Gson 解析的默认值
     *
     * @param defValues 默认值对象
     * @return 默认值
     */
    fun defaultValue(defValues: NullDataValue): HttpGlobalConfig {
        this.defValues = defValues
        return this
    }

    /**
     * if you use this http lib,must call it
     *
     * @param baseUrl RetrofitRequest's baseUrl,and this url will be added to HostnameVerifier
     * @return config self
     */
    fun baseUrl(baseUrl: String): HttpGlobalConfig {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * if you open retry strategy,you can set delay between tow requests
     *
     * @param retryDelay delay time (unit is 'ms')
     * @return config self
     */
    fun retryDelayMillis(retryDelay: Long): HttpGlobalConfig {
        retryDelayMillis = retryDelay
        return this
    }

    /**
     * set retry count
     *
     * @param retryCount retryCount
     * @return config self
     */
    fun retryCount(retryCount: Int): HttpGlobalConfig {
        this.retryCount = retryCount
        return this
    }

    fun interceptor(interceptor: Interceptor): HttpGlobalConfig {
        // 日志拦截器最后添加（避免其他拦截器添加的数据打印缺失）
        if (interceptor is HttpLoggingInterceptor) {
            loggingInterceptor = interceptor
            loggingInterceptor!!.isNetInterceptor = false
        } else {
            interceptors.add(interceptor)
        }
        return this
    }

    fun netInterceptor(netInterceptor: Interceptor): HttpGlobalConfig {
        // 日志拦截器最后添加（避免其他拦截器添加的数据打印缺失）
        if (netInterceptor is HttpLoggingInterceptor) {
            loggingInterceptor = netInterceptor
            loggingInterceptor!!.isNetInterceptor = true
        } else {
            netInterceptors.add(netInterceptor)
        }
        return this
    }

    fun readTimeout(readTimeout: Long): HttpGlobalConfig {
        this.readTimeout = readTimeout
        return this
    }

    fun writeTimeout(writeTimeout: Long): HttpGlobalConfig {
        this.writeTimeout = writeTimeout
        return this
    }

    fun connectTimeout(connectTimeout: Long): HttpGlobalConfig {
        this.connectTimeout = connectTimeout
        return this
    }

    fun apiDataClazz(clazz: Class<out IApiData<*>?>): HttpGlobalConfig {
        apiDataClazz = clazz
        return this
    }

    /**
     * 配置是否允许所有 host
     *
     * @param trustAll 是否允许所有host
     * @return config
     */
    fun trustAllHost(trustAll: Boolean): HttpGlobalConfig {
        isTrustAllHost = trustAll
        return this
    }

    /**
     * 是否强制所有环境 mockJson 生效
     */
    fun alwaysUseMock(alwaysUseMock: Boolean): HttpGlobalConfig {
        isAlwaysUseMock = alwaysUseMock
        return this
    }

    //    ######################################## getter ########################################
    fun getClientBuilder(): OkHttpClient.Builder {
        // 缓存拦截器只添加一次
        if (cache != null) {
            clientBuilder.cache(cache)
        }
        return clientBuilder
    }

    fun getInterceptors(): List<Interceptor> {
        return interceptors
    }

    fun getNetInterceptors(): List<Interceptor> {
        return netInterceptors
    }

    // 默认加密套件
    private val defaultClientBuilder: OkHttpClient.Builder
        get() {
            // 默认加密套件
            val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2).build()
            val specs: MutableList<ConnectionSpec> = ArrayList()
            specs.add(cs)
            specs.add(ConnectionSpec.COMPATIBLE_TLS)
            specs.add(ConnectionSpec.CLEARTEXT)
            return OkHttpClient().newBuilder().connectionSpecs(specs)
        }

    fun getCallAdapterFactories(): List<CallAdapter.Factory> {
        return callAdapterFactories
    }

    fun getGlobalHeaders(): Map<String, String> {
        return globalHeaders
    }

    fun getGlobalParams(): Map<String, String> {
        return globalParams
    }

    fun debug(debug: Boolean): HttpGlobalConfig {
        isDebug = debug
        return this
    }

    fun cacheAll(): Boolean {
        return cacheAll
    }

    fun getApiDataClazz(): Class<*> {
        return apiDataClazz
    }

    fun apiSuccessCode(apiSuccessCode: String): HttpGlobalConfig {
        this.apiSuccessCode = apiSuccessCode
        return this
    }

    fun setContext(context: Context){
        this.context = context
    }

    fun getContext():Context?{
        return context?.applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var singleton: HttpGlobalConfig? = null
            get() {
                if (field == null) {
                    field = HttpGlobalConfig()
                }
                return field
            }

        val instance: HttpGlobalConfig get() = singleton!!
    }
}