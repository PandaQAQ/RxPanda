package com.pandaq.rxpanda.interceptor

import com.pandaq.rxpanda.callbacks.TransmitCallback
import com.pandaq.rxpanda.models.ProgressUploadBody
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :进度监听拦截器
 */
class UploadInterceptor(private val mCallback: TransmitCallback?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = ProgressUploadBody(request.body(), mCallback)
        val finalRequest = request.newBuilder()
            .addHeader("Connection", "alive")
            .method(request.method(), requestBody)
            .build()
        return chain.proceed(finalRequest)
    }
}