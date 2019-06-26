package com.pandaq.rxpanda.annotation;

import com.pandaq.rxpanda.entity.IApiData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huxinyu on 2019/6/19.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :注解指定接口的包裹 ApiData 类型
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiData {
    Class<? extends IApiData> clazz();
}
