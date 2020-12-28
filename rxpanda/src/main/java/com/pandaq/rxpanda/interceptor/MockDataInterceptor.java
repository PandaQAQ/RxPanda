package com.pandaq.rxpanda.interceptor;

import com.pandaq.rxpanda.annotation.MockJson;
import com.pandaq.rxpanda.constants.MediaTypes;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Invocation;

/**
 * Created by huxinyu on 2020/12/27.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class MockDataInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            Method method = invocation.method();
            MockJson mockJson = method.getAnnotation(MockJson.class);
            if (mockJson != null) {
                chain.proceed(request);
                return new Response.Builder()
                        .code(200)
                        .addHeader("Content-Type", "application/json")
                        .body(ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE, mockJson.json()))
                        .message("这是拦截器模拟数据！！！！")
                        .request(chain.request())
                        .protocol(Protocol.HTTP_2)
                        .build();
            }
        }
        // 未注解不进行拦截
        return chain.proceed(request);
    }
}
