package com.pandaq.ktpanda.annotation

/**
 * Created by huxinyu on 2020/12/28.
 * Email : panda.h@foxmail.com
 * Description :
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class MockJson(val json: String)