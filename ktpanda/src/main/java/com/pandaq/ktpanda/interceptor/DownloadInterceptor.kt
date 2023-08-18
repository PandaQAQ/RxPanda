package com.pandaq.ktpanda.interceptor

import com.pandaq.ktpanda.callbacks.DownloadCallBack
import com.pandaq.ktpanda.models.ProgressDownloadBody
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :下载拦截器
 */
class DownloadInterceptor(private val mCallback: DownloadCallBack?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBody = ProgressDownloadBody(response.body(), mCallback)
        return response.newBuilder()
            .body(responseBody)
            .build()
    }
}