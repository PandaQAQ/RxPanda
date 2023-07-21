package com.pandaq.rxpanda.annotation

/**
 * Created by huxinyu on 2021/9/15.
 * Email : panda.h@foxmail.com
 * Description :
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheIt 