package com.pandaq.rxpanda;

/**
 * Created by huxinyu on 2018/5/27.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :http 请求的一些状态码
 */
public class HttpCode {

    /**
     * HTTP 部分为 http 协议 code
     */
    public static class HTTP {

        /**
         * http 请求创建
         */
        public static final int CREATED = 201;
        /**
         * http 请求接受
         */
        public static final int RECEIVED = 202;
        /**
         * 返回未授权内容（服务器已响应但可能返回的另一来源内容）
         */
        public static final int UN_AUTH_CONTENT = 203;
        /**
         * 无内容
         */
        public static final int NONE_CONTENT = 204;
        /**
         * 无内容并要求重置表单信息
         */
        public static final int RESET = 205;
        /**
         * 处理了部分的 get 请求
         */
        public static final int PART_RESOLVED = 206;

        /**
         * 400 错误请求
         */
        public static final int ERROR = 400;
        /**
         * 未授权，需要登陆信息
         */
        public static final int UNAUTHORIZED = 401;
        /**
         * 拒绝访问
         */
        public static final int REFUSE = 403;
        /**
         * 无法找到
         */
        public static final int NOT_FOUND = 404;
        /**
         * 拒绝当前请求方法
         */
        public static final int METHOD_REFUSE = 405;
        /**
         * 请求内容无法找到指定的响应
         */
        public static final int CAN_NOT_RECEIVE = 406;
        /**
         * 服务器等候超时
         */
        public static final int WAIT_TIME_OUT = 408;

        /**
         * 服务器内部错误
         */
        public static final int INNER_EXCEPTION = 500;
        /**
         * 请求接口尚未实现
         */
        public static final int UMIMPLEMENTS = 501;
        /**
         * 网关错误
         */
        public static final int GATEWAY_EXCEPTION = 502;
        /**
         * 服务不可用（宕机或维护中）
         */
        public static final int SERVICE_UNUSEFUL = 503;
        /**
         * 网关超时
         */
        public static final int GATEWAY_TIME_OUT = 504;
        /**
         * http 协议版本不支持
         */
        public static final int HTTP_VERSION_UNSUPPORT = 505;

    }

    /**
     * 请求返回成功后，框架层处理错误.避免与服务端定义错误重复使用负数
     */
    public static class FRAME_WORK {
        //未知错误
        public static final int UNKNOWN = -0x1000;
        //解析错误
        public static final int PARSE_ERROR = -0x1001;
        //网络错误
        public static final int NETWORK_ERROR = -0x1002;
        //证书出错
        public static final int SSL_ERROR = -0x1003;
        //连接超时
        public static final int TIMEOUT_ERROR = -0x1004;
        //解析壳错误
        public static final int SHELL_FORMAT_ERROR = -0x1005;
    }

}
