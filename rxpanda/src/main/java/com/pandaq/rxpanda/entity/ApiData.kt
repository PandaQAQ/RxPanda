package com.pandaq.rxpanda.entity

import com.pandaq.rxpanda.config.HttpGlobalConfig

/**
 * Created by huxinyu on 2018/5/27.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :服务器数据结构框架类
 *
 *
 * service api data recommend：
 *
 *
 * *{
 * *    "code":0,
 * *    "message":"message",
 * *    "data":data Object or data Array
 * *}
 */
class ApiData<T> : IApiData<T> {
    /**
     * 接口请求返回码
     */
    override var code: String? = null

    /**
     * 消息，可为空
     */
    override var msg: String? = null

    /**
     * data 内容
     */
    override val data: T? = null

    override fun isSuccess(): Boolean {
       return if (this.code == null) false else HttpGlobalConfig.instance.apiSuccessCode == this.code
    }

}