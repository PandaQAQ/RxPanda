package com.pandaq.rxpanda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huxinyu on 2019/6/17.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :接口如果不想使用 Data 包裹数据，直接解析得到泛型实体需用此注解标记
 * <p>
 * example：
 *
 * @ RealEntity
 * Observable<RealData> getRealData()
 * 这样可以解析接口直接返回的 RealData 的 json
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RealEntity {
}
