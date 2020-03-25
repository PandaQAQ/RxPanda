package com.pandaq.rxpanda.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pandaq.rxpanda.gsonadapter.DefaultTypeAdapters;

import java.math.BigDecimal;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :创建 Gson 对象单例，使用反射特殊处理了解析 number 类型都为 double 的问题
 */
public class GsonUtil {

    private static Gson gson;

    public static synchronized Gson gson() {
        if (gson == null) {
            gson = create();
        }
        return gson;
    }

    private static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(Boolean.class, DefaultTypeAdapters.BOOLEAN)
                .registerTypeAdapter(Boolean.class, DefaultTypeAdapters.BOOLEAN_AS_STRING)
                .registerTypeAdapter(Integer.class, DefaultTypeAdapters.INTEGER)
                .registerTypeAdapter(Long.class, DefaultTypeAdapters.LONG)
                .registerTypeAdapter(Float.class, DefaultTypeAdapters.FLOAT)
                .registerTypeAdapter(Double.class, DefaultTypeAdapters.DOUBLE)
                .registerTypeAdapter(Number.class, DefaultTypeAdapters.NUMBER)
                .registerTypeAdapter(String.class, DefaultTypeAdapters.STRING)
                .registerTypeAdapter(BigDecimal.class, DefaultTypeAdapters.BIG_DECIMAL)
                .registerTypeAdapter(StringBuilder.class, DefaultTypeAdapters.STRING_BUILDER)
                .registerTypeAdapter(StringBuffer.class, DefaultTypeAdapters.STRING_BUFFER)
                .serializeNulls() //不忽略为 null 的参数
                .create();
    }
}
