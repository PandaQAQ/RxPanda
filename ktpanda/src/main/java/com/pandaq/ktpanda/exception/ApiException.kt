package com.pandaq.ktpanda.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import com.pandaq.ktpanda.HttpCode
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 * Created by huxinyu on 2019/3/8.
 * Email : panda.h@foxmail.com
 * Description :自定义网络异常类
 */
class ApiException : IOException {
    val code: String
    private var msg: String?
    private var data: String? = null
    var exceptionType = ExceptionType.UNKNOWN

    constructor(throwable: Throwable, code: String) : super(throwable) {
        this.code = code
        msg = throwable.message
    }

    constructor(code: String, msg: String?, data: String?) {
        this.code = code
        this.data = data
        this.msg = msg
    }

    fun getMsg(): String {
        return if (message == null) "" else message!!
    }

    fun setMessage(message: String?): ApiException {
        this.msg = message
        return this
    }

    fun getData(): String {
        return if (data == null) "" else data!!
    }

    fun setData(data: String?) {
        this.data = data
    }

    override fun toString(): String {
        return "$message(code:$code)"
    }

    companion object {
        @JvmStatic
        fun handleException(e: Throwable): ApiException {
            val ex: ApiException
            return when (e) {
                is HttpException -> {
                    ex = ApiException(e, e.code().toString())
                    when (e.code()) {
                        HttpCode.HTTP.CAN_NOT_RECEIVE, HttpCode.HTTP.CREATED, HttpCode.HTTP.RECEIVED, HttpCode.HTTP.UN_AUTH_CONTENT, HttpCode.HTTP.NONE_CONTENT, HttpCode.HTTP.RESET, HttpCode.HTTP.PART_RESOLVED, HttpCode.HTTP.ERROR, HttpCode.HTTP.UNAUTHORIZED, HttpCode.HTTP.REFUSE, HttpCode.HTTP.NOT_FOUND, HttpCode.HTTP.METHOD_REFUSE, HttpCode.HTTP.WAIT_TIME_OUT, HttpCode.HTTP.INNER_EXCEPTION, HttpCode.HTTP.UN_IMPLEMENTS, HttpCode.HTTP.GATEWAY_EXCEPTION, HttpCode.HTTP.SERVICE_UNUSEFUL, HttpCode.HTTP.GATEWAY_TIME_OUT, HttpCode.HTTP.HTTP_VERSION_UNSUPPORT -> {
                            ex.exceptionType = ExceptionType.SERVER
                            ex.msg = e.message
                        }

                        else -> {
                            ex.exceptionType = ExceptionType.SERVER
                            ex.msg = e.message
                        }
                    }
                    ex
                }

                is JsonParseException, is JSONException, is ParseException -> {
                    ex = ApiException(e, HttpCode.FRAMEWORK.PARSE_ERROR)
                    ex.msg = e.message
                    ex.exceptionType = ExceptionType.LOCAL
                    ex
                }

                is ConnectException -> {
                    ex = ApiException(e, HttpCode.FRAMEWORK.NETWORK_ERROR)
                    ex.exceptionType = ExceptionType.CONNECT
                    ex.msg = "网络跑丢了，稍后再试"
                    ex
                }

                is SSLHandshakeException -> {
                    ex = ApiException(e, HttpCode.FRAMEWORK.SSL_ERROR)
                    ex.exceptionType = ExceptionType.CONNECT
                    ex.msg = "网络跑丢了，稍后再试"
                    ex
                }

                is SocketTimeoutException -> {
                    ex = ApiException(e, HttpCode.FRAMEWORK.TIMEOUT_ERROR)
                    ex.exceptionType = ExceptionType.CONNECT
                    ex.msg = "网络跑丢了，稍后再试"
                    ex
                }

                is ApiException -> {
                    e.exceptionType = ExceptionType.API
                    e
                }

                else -> {
                    ex = ApiException(e, HttpCode.FRAMEWORK.UNKNOWN)
                    ex.exceptionType = ExceptionType.UNKNOWN
                    ex.msg = e.message
                    ex
                }
            }
        }
    }
}