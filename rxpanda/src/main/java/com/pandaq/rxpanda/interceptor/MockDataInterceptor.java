package com.pandaq.rxpanda.interceptor;

import android.util.Log;

import com.pandaq.rxpanda.annotation.MockJson;
import com.pandaq.rxpanda.constants.MediaTypes;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

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

    // mockResponse 是为直接使用 post/get 方法提供的.与注解不可能同时存在
    private final String localMockJson;

    public MockDataInterceptor(String mockJson) {
        this.localMockJson = mockJson;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // retrofit 接口注解添加方式
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            Method method = invocation.method();
            MockJson mockJson = method.getAnnotation(MockJson.class);
            if (mockJson != null) {
                chain.proceed(request);
                Log.w("MockDataInterceptor", method.getName() + "()  ！！！！！！！！！！！！！！！！！！！！正在使用模拟数据！！！！！！！！！！！！！！！！！！！！");
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
        // http 直接请求方式代码本地添加
        if (localMockJson != null) {
            chain.proceed(request);
            Log.w("MockDataInterceptor", request.url() + "！！！！！！！！！！！！！！！！！！！！正在使用模拟数据！！！！！！！！！！！！！！！！！！！！");
            return new Response.Builder()
                    .code(200)
                    .addHeader("Content-Type", "application/json")
                    .body(ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE, localMockJson))
                    .message("这是拦截器模拟数据！！！！")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build();
        }
        // 未注解不进行拦截
        return chain.proceed(request);
    }
}
