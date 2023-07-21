package com.pandaq.rxpanda

import android.content.Context
import com.pandaq.rxpanda.RequestJobHelper.Companion.get
import com.pandaq.rxpanda.config.HttpGlobalConfig
import com.pandaq.rxpanda.requests.okhttp.GetRequest
import com.pandaq.rxpanda.requests.okhttp.io.DownloadRequest
import com.pandaq.rxpanda.requests.okhttp.io.UploadRequest
import com.pandaq.rxpanda.requests.okhttp.post.PostBodyRequest
import com.pandaq.rxpanda.requests.okhttp.post.PostFormRequest
import com.pandaq.rxpanda.requests.okhttp.post.PostRequest
import com.pandaq.rxpanda.requests.retrofit.RetrofitRequest

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :http request tool class
 */
object RxPanda {
    fun init(context: Context): HttpGlobalConfig {
        val instance = HttpGlobalConfig.instance
        instance.setContext(context)
        return instance
    }

    /**
     * normal get request
     */
    operator fun get(url: String): GetRequest {
        return GetRequest(url!!)
    }

    /**
     * normal post request
     */
    fun post(url: String): PostRequest {
        return PostRequest(url!!)
    }

    /**
     * form post request
     */
    fun postForm(url: String): PostFormRequest {
        return PostFormRequest(url)
    }

    /**
     * body post request
     */
    fun postBody(url: String): PostBodyRequest {
        return PostBodyRequest(url)
    }

    /**
     * download request
     *
     * @param url download url
     */
    fun download(url: String): DownloadRequest {
        return DownloadRequest(url)
    }

    /**
     * 上传文件
     *
     * @param url 上传地址
     * @return return a UploadRequest object
     */
    fun upload(url: String): UploadRequest {
        return UploadRequest(url)
    }

    /**
     * retrofit request
     *
     * @return return a RetrofitRequest object
     */
    fun retrofit(): RetrofitRequest {
        return RetrofitRequest()
    }

    /**
     * 获取到请求管理器
     *
     * @return requestManager
     */
    @JvmStatic
    fun manager(): RequestJobHelper? {
        return get()
    }
}