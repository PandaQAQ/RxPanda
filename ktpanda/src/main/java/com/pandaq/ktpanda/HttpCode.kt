package com.pandaq.ktpanda

/**
 * Created by huxinyu on 2018/5/27.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :http 请求的一些状态码
 */
class HttpCode {
    /**
     * HTTP 部分为 http 协议 code
     */
    object HTTP {
        /**
         * http 请求创建
         */
        const val CREATED = 201

        /**
         * http 请求接受
         */
        const val RECEIVED = 202

        /**
         * 返回未授权内容（服务器已响应但可能返回的另一来源内容）
         */
        const val UN_AUTH_CONTENT = 203

        /**
         * 无内容
         */
        const val NONE_CONTENT = 204

        /**
         * 无内容并要求重置表单信息
         */
        const val RESET = 205

        /**
         * 处理了部分的 get 请求
         */
        const val PART_RESOLVED = 206

        /**
         * 400 错误请求
         */
        const val ERROR = 400

        /**
         * 未授权，需要登陆信息
         */
        const val UNAUTHORIZED = 401

        /**
         * 拒绝访问
         */
        const val REFUSE = 403

        /**
         * 无法找到
         */
        const val NOT_FOUND = 404

        /**
         * 拒绝当前请求方法
         */
        const val METHOD_REFUSE = 405

        /**
         * 请求内容无法找到指定的响应
         */
        const val CAN_NOT_RECEIVE = 406

        /**
         * 服务器等候超时
         */
        const val WAIT_TIME_OUT = 408

        /**
         * 服务器内部错误
         */
        const val INNER_EXCEPTION = 500

        /**
         * 请求接口尚未实现
         */
        const val UN_IMPLEMENTS = 501

        /**
         * 网关错误
         */
        const val GATEWAY_EXCEPTION = 502

        /**
         * 服务不可用（宕机或维护中）
         */
        const val SERVICE_UNUSEFUL = 503

        /**
         * 网关超时
         */
        const val GATEWAY_TIME_OUT = 504

        /**
         * http 协议版本不支持
         */
        const val HTTP_VERSION_UNSUPPORT = 505
    }

    /**
     * 请求返回成功后，框架层处理错误.避免与服务端定义错误重复使用负数
     */
    object FRAMEWORK {
        //未知错误
        const val UNKNOWN = "-1000"

        //解析错误
        const val PARSE_ERROR = "-1001"

        //网络错误
        const val NETWORK_ERROR = "-1002"

        //证书出错
        const val SSL_ERROR = "-1003"

        //连接超时
        const val TIMEOUT_ERROR = "-1004"

        //解析壳错误
        const val SHELL_FORMAT_ERROR = "-1005"
    }
}