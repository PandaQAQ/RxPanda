package com.pandaq.rxpanda.requests.retrofit

import android.text.TextUtils
import com.pandaq.rxpanda.interceptor.CacheInterceptor
import com.pandaq.rxpanda.interceptor.MockDataInterceptor
import com.pandaq.rxpanda.interceptor.ParamsInterceptor
import com.pandaq.rxpanda.interceptor.TimeoutInterceptor
import com.pandaq.rxpanda.requests.Request
import com.pandaq.rxpanda.ssl.SSLManager.SafeHostnameVerifier
import retrofit2.Retrofit

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description : request for use retrofit
 */
open class RetrofitRequest : Request<RetrofitRequest>() {
    // local basUrl
    private var baseUrl = ""
    protected var localParams: MutableMap<String, String> = LinkedHashMap() //请求参数

    /**
     * set request base url
     *
     * @param baseUrl base url
     * @return request
     */
    fun baseUrl(baseUrl: String): RetrofitRequest {
        this.baseUrl = baseUrl
        return this
    }

    fun <T> create(apiService: Class<T>): T {
        injectLocalParams()
        val retrofit: Retrofit = if (!TextUtils.isEmpty(baseUrl)) { // 如果基础地址改了者需要重新构建个 Retrofit 对象，避免影响默认请求的配置
            val newRetrofitBuilder = Retrofit.Builder()
            newRetrofitBuilder.baseUrl(baseUrl)
            globalConfig.converterFactory
            newRetrofitBuilder.addConverterFactory(globalConfig.converterFactory)
            if (globalConfig.getCallAdapterFactories().isNotEmpty()) {
                for (factory in globalConfig.getCallAdapterFactories()) {
                    newRetrofitBuilder.addCallAdapterFactory(factory)
                }
            }
            getClientBuilder().hostnameVerifier(SafeHostnameVerifier(baseUrl))
            // 添加日志拦截器
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
            newRetrofitBuilder.client(globalConfig.getClientBuilder().build().newBuilder().build())
            newRetrofitBuilder.build()
        } else { // 使用默认配置的对象
            commonRetrofit
        }
        return retrofit.create(apiService)
    }

    override fun injectLocalParams() {
        super.injectLocalParams()
        // retrofit 方式请求，通过拦截器添加参数
        localParams.putAll(globalConfig.getGlobalParams())
        if (localParams.isNotEmpty()) {
            val paramsInterceptor = ParamsInterceptor()
            paramsInterceptor.paramsMap = localParams
            if (!getClientBuilder().networkInterceptors().contains(paramsInterceptor)) {
                // 将参数添加到请求中
                getClientBuilder().addNetworkInterceptor(paramsInterceptor)
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
    fun addParam(paramKey: String?, paramValue: String?): RetrofitRequest {
        if (paramKey != null && paramValue != null) {
            localParams[paramKey] = paramValue
        }
        return this
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    fun addParams(params: Map<String, String>?): RetrofitRequest {
        if (params != null) {
            localParams.putAll(params)
        }
        return this
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    fun removeParam(paramKey: String?): RetrofitRequest {
        if (paramKey != null) {
            localParams.remove(paramKey)
        }
        return this
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    fun params(params: MutableMap<String, String>?): RetrofitRequest {
        if (params != null) {
            localParams = params
        }
        return this
    }
}