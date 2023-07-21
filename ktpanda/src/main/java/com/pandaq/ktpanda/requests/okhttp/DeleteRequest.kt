package com.pandaq.ktpanda.requests.okhttp

import com.pandaq.ktpanda.requests.okhttp.base.HttpRequest
import okhttp3.ResponseBody

/**
 * Created by huxinyu on 2019/3/16.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :request in delete method
 */
class DeleteRequest(url: String) : HttpRequest<DeleteRequest>(url) {

    override suspend fun execute(): ResponseBody? {
        return mApi?.delete(url, localParams)
    }

}