package com.pandaq.ktpanda.requests.okhttp

import com.pandaq.ktpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/6/3.
 * Email : panda.h@foxmail.com
 * Description :
 */
class PatchRequest(url: String) : HttpRequest<PatchRequest>(url) {
    override suspend fun execute(): ResponseBody? {
        return mApi?.patch(url, localParams)
    }
}