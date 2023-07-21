package com.pandaq.ktpanda.annotation

import com.pandaq.ktpanda.entity.IApiData
import kotlin.reflect.KClass

/**
 * Created by huxinyu on 2019/6/19.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :注解指定接口的包裹 ApiData 类型
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiData(val clazz: KClass<out IApiData<*>>)