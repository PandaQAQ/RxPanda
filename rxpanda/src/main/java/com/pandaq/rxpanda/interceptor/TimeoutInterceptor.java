package com.pandaq.rxpanda.interceptor;

import com.pandaq.rxpanda.annotation.Timeout;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * Created by huxinyu on 2022/1/11.
 * Email : panda.h@foxmail.com
 * Description :自定义注解的统一转换拦截器
 */
public class TimeoutInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            Method method = invocation.method();
            Timeout timeout = method.getAnnotation(Timeout.class);
            if (timeout != null) {
                Chain timeOutChain;
                timeOutChain = chain.withConnectTimeout(timeout.connectTimeout(), TimeUnit.MILLISECONDS);
                timeOutChain = timeOutChain.withReadTimeout(timeout.readTimeout(), TimeUnit.MILLISECONDS);
                timeOutChain = timeOutChain.withWriteTimeout(timeout.writeTimeout(), TimeUnit.MILLISECONDS);
                return timeOutChain.proceed(request);
            }
        }
        return chain.proceed(request);
    }
}
