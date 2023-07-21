package com.pandaq.rxpanda.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.annotation.CacheIt
import com.pandaq.rxpanda.annotation.IgnoreCache
import com.pandaq.rxpanda.config.HttpGlobalConfig
import com.pandaq.rxpanda.log.HttpLoggingInterceptor
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException

/**
 * Created by huxinyu on 2021/9/15.
 * Email : panda.h@foxmail.com
 * Description :是否使用缓存的拦截器，对添加了不需要缓存注解的接口和IO接口以外的所有接口操作
 */
class CacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // IO 相关的接口直接返回请求结果，不做缓存相关处理
        val ioFlag = request.header(HttpLoggingInterceptor.IO_FLAG_HEADER)
        val isIoRequest = !TextUtils.isEmpty(ioFlag)
        if (isIoRequest) {
            return chain.proceed(request)
        }

        //添加忽略缓存注解的接口直接返回请求结果，不做缓存相关处理
        val invocation = request.tag(Invocation::class.java)
        return if (invocation != null) {
            if (HttpGlobalConfig.instance.cacheAll()) { //全局使用缓存
                val method = invocation.method()
                val ignoreCache = method.getAnnotation(IgnoreCache::class.java)
                if (ignoreCache != null) { //忽略缓存，使用真实数据
                    chain.proceed(request)
                } else {
                    // 返回缓存数据
                    getCacheResponse(chain, request)
                }
            } else { //根据配置使用缓存
                val method = invocation.method()
                val cacheIt = method.getAnnotation(CacheIt::class.java)
                if (cacheIt != null) {
                    // 返回缓存数据
                    getCacheResponse(chain, request)
                } else {
                    chain.proceed(request)
                }
            }
        } else {
            chain.proceed(request)
        }
    }

    /**
     * 构建缓存请求
     *
     * @param chain   请求的 chain
     * @param req 请求体
     * @return 结果
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    private fun getCacheResponse(chain: Interceptor.Chain, req: Request): Response {
        // 一般接口，进行缓存策略处理
        var request = req
        if (!isNetworkConnected) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            return chain.proceed(request)
        }
        val response = chain.proceed(request)
        return if (response.code() != 200) {
            response.newBuilder()
                .header("Cache-Control", CacheControl.FORCE_CACHE.toString()).build()
        } else response
    }

    private val isNetworkConnected: Boolean
        get() {
            val manager = HttpGlobalConfig.instance.getContext()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            for (allNetwork in manager.allNetworks) {
                val info = manager.getNetworkInfo(allNetwork)
                if (info!!.isConnected) {
                    return true
                }
            }
            return false
        }
}