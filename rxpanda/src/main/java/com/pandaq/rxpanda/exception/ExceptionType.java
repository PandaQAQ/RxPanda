package com.pandaq.rxpanda.exception;

/**
 * Created by huxinyu on 2019/8/19.
 * Email : panda.h@foxmail.com
 * Description :异常分类枚举 SERVER 服务器异常 API 接口异常 UNKNOWN 未知异常
 */
public enum ExceptionType {
    /**
     * SERVER: 服务器异常
     * NETWORK：网络异常
     * API: 接口返回的定义异常
     * LOCAL：本地处理抛出异常
     * UNKNOWN: 未知异常
     */
    SERVER, NETWORK, API, LOCAL, UNKNOWN
}
