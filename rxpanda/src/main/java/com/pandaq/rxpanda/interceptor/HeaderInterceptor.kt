package com.pandaq.rxpanda.interceptor

import androidx.annotation.NonNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :请求头拦截器，为每一次请求添加请求头
 */
class HeaderInterceptor(
    @param:NonNull private val headers: Map<String, String>
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (checkNotNull(headers)) {
            for ((key, value) in headers) {
                builder.header(key, value)
            }
        }
        return chain.proceed(builder.build())
    }

    private fun checkNotNull(map: Map<*, *>?): Boolean {
        return map != null && map.isNotEmpty()
    }
}