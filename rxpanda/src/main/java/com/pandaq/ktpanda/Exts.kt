package com.pandaq.ktpanda

import com.pandaq.ktpanda.exception.ApiException
import com.pandaq.ktpanda.exception.ExceptionType
import com.pandaq.ktpanda.utils.GsonUtil
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by huxinyu on 2023/7/21.
 * Email : panda.h@foxmail.com
 * Description :
 */

@SuppressWarnings("unchecked")
fun <T> ResponseBody.cast(clazz: Class<T>): T {
    val json: String = this.string()
    return if (json.isEmpty()) {
        val exception = ApiException("-1", "data parse error", json)
        exception.exceptionType = ExceptionType.JSON_PARSE
        throw exception
    } else {
        val type: Type = (clazz.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        GsonUtil.gson().fromJson(json, type)
    }
}
