package com.pandaq.rxpanda.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pandaq.rxpanda.gsonadapter.DefaultTypeAdapters
import java.math.BigDecimal

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :创建 Gson 对象单例，使用反射特殊处理了解析 number 类型都为 double 的问题
 */
object GsonUtil {
    private var gson: Gson? = null
    @Synchronized
    fun gson(): Gson {
        if (gson == null) {
            gson = create()
        }
        return gson!!
    }

    private fun create(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, DefaultTypeAdapters.BOOLEAN)
            .registerTypeAdapter(Boolean::class.java, DefaultTypeAdapters.BOOLEAN_AS_STRING)
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, DefaultTypeAdapters.BOOLEAN)
            .registerTypeAdapter(Int::class.java, DefaultTypeAdapters.INTEGER)
            .registerTypeAdapter(Int::class.javaPrimitiveType, DefaultTypeAdapters.INTEGER)
            .registerTypeAdapter(Long::class.java, DefaultTypeAdapters.LONG)
            .registerTypeAdapter(Long::class.javaPrimitiveType, DefaultTypeAdapters.LONG)
            .registerTypeAdapter(Float::class.java, DefaultTypeAdapters.FLOAT)
            .registerTypeAdapter(Float::class.javaPrimitiveType, DefaultTypeAdapters.FLOAT)
            .registerTypeAdapter(Double::class.java, DefaultTypeAdapters.DOUBLE)
            .registerTypeAdapter(Double::class.javaPrimitiveType, DefaultTypeAdapters.DOUBLE)
            .registerTypeAdapter(Number::class.java, DefaultTypeAdapters.NUMBER)
            .registerTypeAdapter(String::class.java, DefaultTypeAdapters.STRING)
            .registerTypeAdapter(
                BigDecimal::class.java,
                DefaultTypeAdapters.BIG_DECIMAL
            ) //                .serializeNulls() //不忽略为 null 的参数
            .create()
    }
}