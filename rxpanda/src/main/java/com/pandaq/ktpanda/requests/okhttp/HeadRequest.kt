package com.pandaq.ktpanda.requests.okhttp

import com.pandaq.ktpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/3/16.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :
 */
class HeadRequest(url: String) : HttpRequest<HeadRequest>(url) {
    override suspend fun execute(): ResponseBody? {
        return mApi?.head(url, localParams)
    }
}