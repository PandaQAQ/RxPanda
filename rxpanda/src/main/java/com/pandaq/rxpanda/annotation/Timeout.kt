package com.pandaq.rxpanda.annotation

/**
 * Created by huxinyu on 2022/1/11.
 * Email : panda.h@foxmail.com
 * Description : 单个接口注解添加超时时间
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Timeout(
    val connectTimeout: Int = 20000,
    val readTimeout: Int = 20000,
    val writeTimeout: Int = 20000
)