package com.pandaq.rxpanda.requests

import android.text.TextUtils
import com.pandaq.rxpanda.config.CONFIG
import com.pandaq.rxpanda.config.HttpGlobalConfig
import com.pandaq.rxpanda.interceptor.CacheInterceptor
import com.pandaq.rxpanda.interceptor.HeaderInterceptor
import com.pandaq.rxpanda.interceptor.MockDataInterceptor
import com.pandaq.rxpanda.interceptor.TimeoutInterceptor
import com.pandaq.rxpanda.ssl.SSLManager
import com.pandaq.rxpanda.ssl.SSLManager.SafeHostnameVerifier
import com.pandaq.rxpanda.utils.CastUtils
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :base http request
 */
open class Request<T : Request<T>> {
    // local readTimeout
    private var readTimeout = 0L

    // local writeTimeout
    private var writeTimeout = 0L

    // local connectTimeout
    private var connectTimeout = 0L
    private val headers: MutableMap<String, String> = LinkedHashMap()
    private val interceptors: MutableList<Interceptor> = ArrayList()
    private val networkInterceptors: MutableList<Interceptor> = ArrayList()

    @JvmField
    protected var clientBuilder: OkHttpClient.Builder? = null

    init {
        clientBuilder = null
    }

    /**
     * 获取子类指定的模拟数据，默认为空
     */
    protected open var mockJson: String? = null

    /**
     * 添加 header map
     *
     * @param headers 请求头 map
     * @return 请求体
     */
    fun addHeaders(headers: Map<String, String>): T {
        this.headers.putAll(headers)
        return CastUtils.cast(this)
    }

    /**
     * 添加 header map
     *
     * @param key   请求头 key
     * @param value 请求头 value
     * @return 请求体
     */
    fun addHeader(key: String, value: String): T {
        headers[key] = value
        return CastUtils.cast(this)
    }

    /**
     * 设置 header map，会覆盖之前的 header
     *
     * @param headers 请求头 map
     * @return 请求体
     */
    fun resetHeader(headers: Map<String, String>): T {
        this.headers.clear()
        this.headers.putAll(headers)
        return CastUtils.cast(this)
    }

    /**
     * 移除指定 header
     *
     * @param key 请求头 key
     * @return 请求体
     */
    fun removeHeader(key: String): T {
        headers.remove(key)
        return CastUtils.cast(this)
    }

    /**
     * 移除全部 header
     *
     * @return 请求体
     */
    fun cleanHeader(): T {
        headers.clear()
        return CastUtils.cast(this)
    }

    //    ##################### interceptors ####################
    fun interceptor(interceptor: Interceptor): T {
        interceptors.add(interceptor)
        return CastUtils.cast(this)
    }

    fun netInterceptor(interceptor: Interceptor): T {
        networkInterceptors.add(interceptor)
        return CastUtils.cast(this)
    }

    fun clearInterceptor(): T {
        interceptors.clear()
        return CastUtils.cast(this)
    }

    fun clearNetInterceptor(): T {
        networkInterceptors.clear()
        return CastUtils.cast(this)
    }

    fun readTimeout(readTimeout: Long): T {
        this.readTimeout = readTimeout
        return CastUtils.cast(this)
    }

    fun writeTimeout(writeTimeout: Long): T {
        this.writeTimeout = writeTimeout
        return CastUtils.cast(this)
    }

    fun connectTimeout(connectTimeout: Long): T {
        this.connectTimeout = connectTimeout
        return CastUtils.cast(this)
    }

    /**
     * 使用全局配置覆盖当前配置
     */
    private fun resetGlobalParams() {
        // http client config
        if (globalConfig.connectionPool == null) {
            globalConfig.connectionPool(
                ConnectionPool(
                    CONFIG.DEFAULT_MAX_IDLE_CONNECTIONS,
                    CONFIG.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS
                )
            )
        }
        globalConfig.connectionPool?.let { globalConfig.connectionPool(it) }
        if (globalConfig.hostnameVerifier == null) {
            globalConfig.hostVerifier(SafeHostnameVerifier(globalConfig.baseUrl))
        }
        globalConfig.hostnameVerifier?.let { getClientBuilder().hostnameVerifier(it) }
        if (globalConfig.sslSocketFactory == null) {
            globalConfig.sslFactory(SSLManager.getSslSocketFactory(null, null, null))
        }
        globalConfig.sslSocketFactory?.let { getClientBuilder().sslSocketFactory(it) }
        getClientBuilder().connectTimeout(globalConfig.connectTimeout, TimeUnit.MILLISECONDS)
        getClientBuilder().readTimeout(globalConfig.readTimeout, TimeUnit.MILLISECONDS)
        getClientBuilder().writeTimeout(globalConfig.writeTimeout, TimeUnit.MILLISECONDS)
        // 添加全局的拦截器
        for (interceptor in globalConfig.getInterceptors()) {
            if (!getClientBuilder().interceptors().contains(interceptor)) {
                getClientBuilder().addInterceptor(interceptor)
            }
        }
        for (interceptor in globalConfig.getNetInterceptors()) {
            if (!getClientBuilder().networkInterceptors().contains(interceptor)) {
                getClientBuilder().addNetworkInterceptor(interceptor)
            }
        }
        getClientBuilder().retryOnConnectionFailure(true)
    }

    /**
     * å
     * 注入本地配置参数
     */
    protected open fun injectLocalParams() {
        // 注入本地配置前先重置现有配置为全局默认配置
        resetGlobalParams()
        // 添加请求头
        globalConfig.getGlobalHeaders().let {
            // 全局的请求头设置进去,将全局加入到本地 header 中（本地同名覆盖全局）
            headers.putAll(it)
        }
        if (headers.isNotEmpty()) {
            getClientBuilder().addInterceptor(HeaderInterceptor(headers))
        }
        // 添加请求拦截器
        if (interceptors.isNotEmpty()) {
            for (interceptor in interceptors) {
                if (!getClientBuilder().interceptors().contains(interceptor)) {
                    getClientBuilder().addInterceptor(interceptor)
                }
            }
        }
        // 添加请求网络拦截器
        if (networkInterceptors.isNotEmpty()) {
            for (interceptor in networkInterceptors) {
                if (!getClientBuilder().networkInterceptors().contains(interceptor)) {
                    getClientBuilder().addInterceptor(interceptor)
                }
            }
        }
        //设置局部超时时间和重试次数
        if (readTimeout > 0) {
            getClientBuilder().readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        }
        if (writeTimeout > 0) {
            getClientBuilder().writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
        }
        if (connectTimeout > 0) {
            getClientBuilder().connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        }
    }
    /**
     * 获取全局的通用配置 Retrofit 对象
     *
     * @return retrofit
     */
    protected val commonRetrofit: Retrofit
        get() {
            // retrofit config
            val retrofitBuilder = Retrofit.Builder()
            if (!TextUtils.isEmpty(globalConfig.baseUrl)) {
                retrofitBuilder.baseUrl(globalConfig.baseUrl.toString())
            } else {
                throw IllegalArgumentException("base url can not be empty !")
            }
            globalConfig.converterFactory
            retrofitBuilder.addConverterFactory(globalConfig.converterFactory)
            for (factory in globalConfig.getCallAdapterFactories()) {
                retrofitBuilder.addCallAdapterFactory(factory)
            }
            // 添加日志拦截器，为网络拦截器
            globalConfig.loggingInterceptor?.let {
                if (it.isNetInterceptor) {
                    getClientBuilder().addNetworkInterceptor(it)
                } else {
                    getClientBuilder().addInterceptor(it)
                }
            }

            // 缓存拦截器
            getClientBuilder().addInterceptor(CacheInterceptor())

            // 时长拦截器
            getClientBuilder().addInterceptor(TimeoutInterceptor())

            // 添加调试阶段的模拟数据拦截器
            if (globalConfig.isAlwaysUseMock || globalConfig.isDebug) {
                val dataInterceptor = MockDataInterceptor()
                dataInterceptor.setLocalMockJson(mockJson)
                getClientBuilder().addNetworkInterceptor(dataInterceptor)
            }
            val client = getClientBuilder().build()
            return retrofitBuilder.client(client).build()
        }
    protected val globalConfig: HttpGlobalConfig
        get() = HttpGlobalConfig.instance

    protected fun getClientBuilder(): OkHttpClient.Builder {
        // GlobalConfig 中的 builder 只用于保存属性，request 根据其属性各自创建新的 builder
        if (clientBuilder == null) {
            clientBuilder = globalConfig.getClientBuilder().build().newBuilder()
        }
        return clientBuilder!!
    }
}