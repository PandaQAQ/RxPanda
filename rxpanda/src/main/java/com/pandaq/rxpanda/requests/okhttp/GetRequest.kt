package com.pandaq.rxpanda.requests.okhttp

import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.cast
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.reflect.Type

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