package com.pandaq.ktpanda.exception

/**
 * Created by huxinyu on 2019/8/19.
 * Email : panda.h@foxmail.com
 * Description :异常分类枚举 SERVER 服务器异常 API 接口异常 UNKNOWN 未知异常
 */
enum class ExceptionType {
    /**
     * SERVER: 服务器异常
     * NETWORK：网络异常 (废弃)
     * API: 接口返回的定义异常
     * LOCAL：本地处理抛出异常
     * UNKNOWN: 未知异常
     * CONNECT: 服务器连接异常（有网环境，接口超时，SSL 错误等）
     * JSON_PARSE: 服务器返回数据解析为本地数据结构出错
     */
    SERVER, @Deprecated("")
    NETWORK, API, LOCAL, UNKNOWN, CONNECT, JSON_PARSE
}