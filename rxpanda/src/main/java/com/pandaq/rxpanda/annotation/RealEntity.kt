package com.pandaq.rxpanda.annotation

/**
 * Created by huxinyu on 2019/6/17.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :接口如果不想使用 Data 包裹数据，直接解析得到泛型实体需用此注解标记
 * example：
 * 这样可以解析接口直接返回的 RealData 的 json
 *
 *
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class RealEntity 