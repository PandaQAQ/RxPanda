package com.pandaq.ktpanda.interceptor

import android.content.Context
import android.net.ConnectivityManager
import com.pandaq.ktpanda.annotation.CacheIt
import com.pandaq.ktpanda.config.HttpGlobalConfig
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException

/**
 * Created by huxinyu on 2021/9/15.
 * Email : panda.h@foxmail.com
 * Description :是否使用缓存的拦截器，只对添加了需要缓存注解的接口进行缓存操作
 */
class CacheNeededInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var needCache = false
        val invocation = request.tag(Invocation::class.java)
        if (invocation != null) {
            val method = invocation.method()
            val cacheIt = method.getAnnotation(CacheIt::class.java)
            needCache = cacheIt != null
        }
        if (!needCache) {
            return chain.proceed(request)
        }
        // 一般接口，进行缓存策略处理
        if (!isNetworkConnected()) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            return chain.proceed(request)
        }
        val response = chain.proceed(request)
        return if (response.code() != 200) {
            response.newBuilder()
                .header("Cache-Control", CacheControl.FORCE_CACHE.toString()).build()
        } else response
    }

    private fun isNetworkConnected(): Boolean {
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