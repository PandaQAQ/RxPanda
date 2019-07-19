package com.pandaq.rxpanda.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.pandaq.rxpanda.gsonadapter.DefaultTypeAdapters;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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
                .registerTypeAdapter(Byte.class, DefaultTypeAdapters.BYTE)
                .registerTypeAdapter(Short.class, DefaultTypeAdapters.SHORT)
                .registerTypeAdapter(Integer.class, DefaultTypeAdapters.INTEGER)
                .registerTypeAdapter(Long.class, DefaultTypeAdapters.LONG)
                .registerTypeAdapter(Float.class, DefaultTypeAdapters.FLOAT)
                .registerTypeAdapter(Double.class, DefaultTypeAdapters.DOUBLE)
                .registerTypeAdapter(Number.class, DefaultTypeAdapters.NUMBER)
                .registerTypeAdapter(String.class, DefaultTypeAdapters.STRING)
                .registerTypeAdapter(BigDecimal.class, DefaultTypeAdapters.BIG_DECIMAL)
                .registerTypeAdapter(StringBuilder.class, DefaultTypeAdapters.STRING_BUILDER)
                .registerTypeAdapter(StringBuffer.class, DefaultTypeAdapters.STRING_BUFFER)
                .create();
    }
}
