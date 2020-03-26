package com.pandaq.rxpanda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * Created by huxinyu on 2020/3/25.
 * Email : panda.h@foxmail.com
 * Description :用于标记实体类是否对接口未返回的数据进行默认值补全
 * <p>
 * ex 实体类为 ：
 * public class UserInfo {
 * <p>
 * private int userId;
 * private String userName;
 * private String nickName;
 * <p>
 * }
 * <p>
 * 接口 json 为：
 * {
 * "userId": "999",
 * }
 * <p>
 * 解析后为
 * <p>
 * {
 * "userId": "999",
 * "userName": "",
 * "nickName": ""
 * }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWired {

}
