package com.pandaq.rxpanda.interceptor

import androidx.annotation.NonNull
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.IOException

/**
 * Created by huxinyu on 2019/8/19.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :公共参数拦截器，为 retrofit 请求添加公共参数
 */
class ParamsInterceptor : Interceptor {
    var paramsMap: Map<String, String> = HashMap()

    /**
     * only support post and get request
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBuilder = request.newBuilder()
        if (paramsMap.isEmpty()) {
            return chain.proceed(request)
        }
        if (request.method() == POST) {
            if (request.body() is FormBody) {           // 表单方式提交
                val newFormBodyBuilder = FormBody.Builder()
                for (o in paramsMap.entries) {
                    val (key, value) = o
                    newFormBodyBuilder.add(key, value)
                }
                val oldFormBody = request.body() as FormBody?
                val paramSize = oldFormBody!!.size()
                for (i in 0 until paramSize) {
                    newFormBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i))
                }
                requestBuilder.post(newFormBodyBuilder.build())
            } else if (request.body() is MultipartBody) {           // MultipartBody 方式提交
                val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                for (o in paramsMap.entries) {
                    val (key, value) = o
                    multipartBuilder.addFormDataPart(key, value)
                }
                val oldParts = (request.body() as MultipartBody?)!!.parts()
                for (part in oldParts) {
                    multipartBuilder.addPart(part)
                }
                requestBuilder.post(multipartBuilder.build())
            } else if (request.body() != null && request.body()!!.contentLength() == 0L) {
                //无参时提交公共参数
                val newFormBodyBuilder = FormBody.Builder()
                for (o in paramsMap.entries) {
                    val (key, value) = o
                    newFormBodyBuilder.add(key, value)
                }
                requestBuilder.post(newFormBodyBuilder.build())
            }
        } else if (request.method() == GET) {
            val builder = request.url().newBuilder()
            for ((key, value) in paramsMap) {
                builder.setQueryParameter(key, value)
            }
            requestBuilder.url(builder.build())
        }
        request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val POST = "POST"
        private const val GET = "GET"
    }
}