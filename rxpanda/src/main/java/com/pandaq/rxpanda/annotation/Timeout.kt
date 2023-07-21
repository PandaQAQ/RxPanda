package com.pandaq.rxpanda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huxinyu on 2022/1/11.
 * Email : panda.h@foxmail.com
 * Description : 单个接口注解添加超时时间
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {

    int connectTimeout() default 20000;

    int readTimeout() default 20000;

    int writeTimeout() default 20000;
}
