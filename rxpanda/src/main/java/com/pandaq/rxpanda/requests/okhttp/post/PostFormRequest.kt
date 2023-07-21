package com.pandaq.rxpanda.requests.okhttp.post

import com.pandaq.rxpanda.RxPanda.manager
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/3/15.
 * Email : panda.h@foxmail.com
 * Description :表单方式 post
 */
class PostFormRequest(url: String) : HttpRequest<PostFormRequest>(url) {
    // 表单方式 post
    private val form: MutableMap<String, Any> = LinkedHashMap()

    // url中带参数 post
    private val stringBuilder = StringBuilder()

    /**
     * post url 中添加参数
     *
     * @param paramKey   key
     * @param paramValue value
     * @return self
     */
    fun urlParams(paramKey: String?, paramValue: String?): PostFormRequest {
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

    /**
     * @param paramKey   表单数据 key
     * @param paramValue 表单数据 value
     * @return self
     */
    fun formParams(paramKey: String?, paramValue: Any?): PostFormRequest {
        if (paramKey != null && paramValue != null) {
            form[paramKey] = paramValue
        }
        return this
    }

    override suspend fun execute(): ResponseBody? {
        if (stringBuilder.isNotEmpty()) {
            url += stringBuilder.toString()
        }
        if (localParams.isNotEmpty()) {
            form.putAll(localParams)
        }
        return mApi?.postForm(url,form)
    }
}