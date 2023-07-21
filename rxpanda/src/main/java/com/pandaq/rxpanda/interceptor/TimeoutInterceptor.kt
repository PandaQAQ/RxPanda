package com.pandaq.rxpanda.interceptor

import com.pandaq.rxpanda.annotation.Timeout
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by huxinyu on 2022/1/11.
 * Email : panda.h@foxmail.com
 * Description :自定义注解的统一转换拦截器
 */
class TimeoutInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java)
        if (invocation != null) {
            val method = invocation.method()
            val timeout = method.getAnnotation(
                Timeout::class.java
            )
            if (timeout != null) {
                var timeOutChain: Interceptor.Chain =
                    chain.withConnectTimeout(timeout.connectTimeout, TimeUnit.MILLISECONDS)
                timeOutChain =
                    timeOutChain.withReadTimeout(timeout.readTimeout, TimeUnit.MILLISECONDS)
                timeOutChain =
                    timeOutChain.withWriteTimeout(timeout.writeTimeout, TimeUnit.MILLISECONDS)
                return timeOutChain.proceed(request)
            }
        }
        return chain.proceed(request)
    }
}