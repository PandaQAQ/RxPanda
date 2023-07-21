package com.pandaq.rxpanda.entity

/**
 * Created by huxinyu on 2019/6/18.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :
 */
interface IApiData<T> {
    val code: String?
    val msg: String?
    val data: T?

    fun isSuccess():Boolean
}