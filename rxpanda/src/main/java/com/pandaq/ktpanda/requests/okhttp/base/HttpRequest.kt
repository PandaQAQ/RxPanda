package com.pandaq.ktpanda.requests.okhttp.base

import android.text.TextUtils
import com.pandaq.ktpanda.api.Api
import com.pandaq.ktpanda.cast
import com.pandaq.ktpanda.requests.Request
import com.pandaq.ktpanda.utils.CastUtils
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description : baseRequest for use okHttp lib, also can return an observable response
 */
abstract class HttpRequest<R : HttpRequest<R>>(url: String) : Request<R>() {
    // http api,兼容 rxJava 观察者模式，需要返回观察对象时，将请求转换成 Retrofit 去请求
    protected var mApi: Api? = null
    protected var url = ""
    protected var localParams: MutableMap<String, String> = LinkedHashMap() //请求参数
    override var mockJson: String? = null

    init {
        if (!TextUtils.isEmpty(url)) {
            this.url = url
        }
        clientBuilder = null
    }

    suspend fun <T> request(clazz: Class<T>): T? {
        injectLocalParams()
        return execute()?.cast(clazz)
    }

    protected abstract suspend fun execute(): ResponseBody?

    /**
     * 添加请求的模拟数据
     */
    fun mockData(mockJson: String?): R {
        this.mockJson = mockJson
        return CastUtils.cast(this)
    }

    /**
     * 添加请求参数
     *
     * @param paramKey
     * @param paramValue
     * @return
     */
    fun addParam(paramKey: String?, paramValue: String?): R {
        if (paramKey != null && paramValue != null) {
            localParams[paramKey] = paramValue
        }
        return CastUtils.cast(this)
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    fun addParams(params: Map<String, String>?): R {
        if (params != null) {
            localParams.putAll(params)
        }
        return CastUtils.cast(this)
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    fun removeParam(paramKey: String?): R {
        if (paramKey != null) {
            localParams.remove(paramKey)
        }
        return CastUtils.cast(this)
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    fun params(params: MutableMap<String, String>?): R {
        if (params != null) {
            localParams = params
        }
        return CastUtils.cast(this)
    }

    override fun injectLocalParams() {
        super.injectLocalParams()
        localParams.putAll(globalConfig.getGlobalParams())
        mApi = commonRetrofit.create(Api::class.java)
    }

}