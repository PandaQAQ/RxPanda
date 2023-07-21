package com.pandaq.ktpanda.interceptor

import android.util.Log
import com.pandaq.ktpanda.annotation.MockJson
import com.pandaq.ktpanda.constants.MediaTypes
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Invocation
import java.io.IOException

/**
 * Created by huxinyu on 2020/12/27.
 * Email : panda.h@foxmail.com
 * Description :
 */
class MockDataInterceptor : Interceptor {
    // mockResponse 是为直接使用 post/get 方法提供的.与注解不可能同时存在
    private var localMockJson: String? = null
    fun setLocalMockJson(localMockJson: String?) {
        this.localMockJson = localMockJson
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // retrofit 接口注解添加方式
        val invocation = request.tag(Invocation::class.java)
        if (invocation != null) {
            val method = invocation.method()
            val mockJson = method.getAnnotation(MockJson::class.java)
            if (mockJson != null) {
                chain.proceed(request)
                Log.w(
                    "MockDataInterceptor",
                    method.name + "()  ！！！！！！！！！！！！！！！！！！！！正在使用模拟数据！！！！！！！！！！！！！！！！！！！！"
                )
                return Response.Builder()
                    .code(200)
                    .addHeader("Content-Type", "application/json")
                    .body(ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE, mockJson.json))
                    .message("这是拦截器模拟数据！！！！")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build()
            }
        }
        // http 直接请求方式代码本地添加
        if (localMockJson != null) {
            chain.proceed(request)
            Log.w(
                "MockDataInterceptor",
                request.url()
                    .toString() + "！！！！！！！！！！！！！！！！！！！！正在使用模拟数据！！！！！！！！！！！！！！！！！！！！"
            )
            return Response.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(localMockJson?.let {
                    ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE,
                        it
                    )
                })
                .message("这是拦截器模拟数据！！！！")
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .build()
        }
        // 未注解不进行拦截
        return chain.proceed(request)
    }
}