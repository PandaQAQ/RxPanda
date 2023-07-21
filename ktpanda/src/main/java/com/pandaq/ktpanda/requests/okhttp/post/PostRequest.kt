package com.pandaq.ktpanda.requests.okhttp.post

import com.pandaq.ktpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :a sample request
 */
class PostRequest(url: String) : HttpRequest<PostRequest>(url) {
    // url中带参数 post
    private val stringBuilder = StringBuilder()

    override suspend fun execute(): ResponseBody? {
        if (stringBuilder.isNotEmpty()) {
            url += stringBuilder.toString()
        }
        return mApi?.post(url, localParams)
    }

    /**
     * post url 中添加参数
     *
     * @param paramKey   key
     * @param paramValue value
     * @return self
     */
    fun urlParams(paramKey: String?, paramValue: String?): PostRequest {
        if (paramKey != null && paramValue != null) {
            if (stringBuilder.isEmpty()) {
                stringBuilder.append("?")
            } else {
                stringBuilder.append("&")
            }
            stringBuilder.append(paramKey).append("=").append(paramValue)
        }
        return this
    }
}