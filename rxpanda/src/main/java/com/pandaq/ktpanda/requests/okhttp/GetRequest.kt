package com.pandaq.ktpanda.requests.okhttp

import com.pandaq.ktpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description : get Request
 */
class GetRequest(url: String) : HttpRequest<GetRequest>(url) {

    override suspend fun execute(): ResponseBody? {
        return mApi?.get(url, localParams)
    }

}