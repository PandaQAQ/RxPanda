package com.pandaq.rxpanda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huxinyu on 2020/11/23.
 * Email : panda.h@foxmail.com
 * Description :标识接口返回数据是 ZLib 压缩后的数据。此类接口数据返回后将会先进行解压再解析为对应的数据
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZLibRequest {
}
