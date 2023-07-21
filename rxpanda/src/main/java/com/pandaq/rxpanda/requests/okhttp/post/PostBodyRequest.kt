package com.pandaq.rxpanda.requests.okhttp.post

import com.pandaq.rxpanda.constants.MediaTypes
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest
import com.pandaq.rxpanda.utils.GsonUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by huxinyu on 2019/3/15.
 * Email : panda.h@foxmail.com
 * Description :
 */
class PostBodyRequest(url: String) : HttpRequest<PostBodyRequest>(url) {
    // url中带参数 post
    private val stringBuilder = StringBuilder()

    // body Post
    private var requestBody: RequestBody? = null

    // body post 参数类型
    private var mediaType: MediaType? = null

    // body post 内容
    private var content: String? = null

    /**
     * post url 中添加参数
     *
     * @param paramKey   key
     * @param paramValue value
     * @return self
     */
    fun urlParam(paramKey: String?, paramValue: String?): PostBodyRequest {
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

    fun setRequestBody(requestBody: RequestBody?): PostBodyRequest {
        this.requestBody = requestBody
        return this
    }

    /**
     * 设置 postBody 对象
     *
     * @param body post 对象
     * @return self
     */
    fun setBody(body: Any?): PostBodyRequest {
        content = GsonUtil.gson().toJson(body)
        mediaType = MediaTypes.APPLICATION_JSON_TYPE
        return this
    }

    fun setString(string: String?): PostBodyRequest {
        content = string
        mediaType = MediaTypes.TEXT_PLAIN_TYPE
        return this
    }

    fun setHtml(string: String?): PostBodyRequest {
        content = string
        mediaType = MediaTypes.TEXT_HTML_TYPE
        return this
    }

    fun setString(string: String?, mediaType: MediaType?): PostBodyRequest {
        content = string
        this.mediaType = mediaType
        return this
    }

    fun setXml(xml: String?): PostBodyRequest {
        content = xml
        mediaType = MediaTypes.APPLICATION_XML_TYPE
        return this
    }

    fun setXmlText(xml: String?): PostBodyRequest {
        content = xml
        mediaType = MediaTypes.TEXT_XML_TYPE
        return this
    }

    fun setJson(json: String?): PostBodyRequest {
        content = json
        mediaType = MediaTypes.APPLICATION_JSON_TYPE
        return this
    }

    fun setJson(jsonObject: JSONObject): PostBodyRequest {
        content = jsonObject.toString()
        mediaType = MediaTypes.APPLICATION_JSON_TYPE
        return this
    }

    fun jsonArray(jsonArray: JSONArray): PostBodyRequest {
        content = jsonArray.toString()
        mediaType = MediaTypes.APPLICATION_JSON_TYPE
        return this
    }

    override suspend fun execute(): ResponseBody? {
        if (stringBuilder.isNotEmpty()) {
            url += stringBuilder.toString()
        }
        requestBody?.let {
            return mApi?.postBody(url, requestBody)
        }
        content?.let {
            if (mediaType != null) {
                requestBody = RequestBody.create(mediaType, it)
                return mApi?.postBody(url, requestBody)
            }
        }
        return mApi?.post(url, localParams)
    }
}