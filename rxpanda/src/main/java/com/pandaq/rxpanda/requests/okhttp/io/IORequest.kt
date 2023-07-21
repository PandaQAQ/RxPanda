package com.pandaq.rxpanda.requests.okhttp.io

import android.text.TextUtils
import androidx.annotation.NonNull
import com.pandaq.rxpanda.api.Api
import com.pandaq.rxpanda.callbacks.TransmitCallback
import com.pandaq.rxpanda.log.HttpLoggingInterceptor
import com.pandaq.rxpanda.requests.Request
import com.pandaq.rxpanda.utils.CastUtils
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
abstract class IORequest<R : IORequest<R>>(url: String) : Request<R>() {
    // http api,兼容 rxJava 观察者模式，需要返回观察对象时，将请求转换成 Retrofit 去请求
    @JvmField
    protected var mApi: Api? = null

    @JvmField
    protected var url = ""

    @JvmField
    protected var localParams: MutableMap<String, String> = LinkedHashMap() //请求参数

    init {
        if (!TextUtils.isEmpty(url)) {
            this.url = url
        }
        clientBuilder = null
        addHeader(HttpLoggingInterceptor.IO_FLAG_HEADER, "IORequest ignore print log !!!")
    }

    protected abstract suspend fun execute(): ResponseBody?

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