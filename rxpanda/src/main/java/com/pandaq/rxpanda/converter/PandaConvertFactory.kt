package com.pandaq.rxpanda.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pandaq.rxpanda.annotation.ApiData
import com.pandaq.rxpanda.annotation.RealEntity
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.BooleanResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.ByteResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.CharacterResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.DoubleResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.FloatResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.IntegerResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.LongResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.ShortResponseBodyConverter
import com.pandaq.rxpanda.converter.ScalarResponseBodyConverters.StringResponseBodyConverter
import com.pandaq.rxpanda.utils.GsonUtil.gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :自定义解析工厂类
 */
class PandaConvertFactory private constructor(private val gson: Gson) : Converter.Factory() {
    /**
     * 这里这个 type 是本地接口方法中传入的泛型类型
     *
     * @param type        Retrofit 接口传入的泛型对象
     * @param annotations Retrofit 接口的注解
     * @param retrofit    Retrofit 对象
     * @return 转换器
     */
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        for (annotation in annotations) {
            // PandaResponseBodyConverter 转换时不使用 ApiData 剥壳，直接转换为接口中定义的对象
            if (annotation is RealEntity) {
                return if (type === String::class.java) {
                    StringResponseBodyConverter.INSTANCE
                } else if (type === Boolean::class.java || type === java.lang.Boolean.TYPE) {
                    BooleanResponseBodyConverter.INSTANCE
                } else if (type === Byte::class.java || type === java.lang.Byte.TYPE) {
                    ByteResponseBodyConverter.INSTANCE
                } else if (type === Char::class.java || type === Character.TYPE) {
                    CharacterResponseBodyConverter.INSTANCE
                } else if (type === Double::class.java || type === java.lang.Double.TYPE) {
                    DoubleResponseBodyConverter.INSTANCE
                } else if (type === Float::class.java || type === java.lang.Float.TYPE) {
                    FloatResponseBodyConverter.INSTANCE
                } else if (type === Int::class.java || type === Integer.TYPE) {
                    IntegerResponseBodyConverter.INSTANCE
                } else if (type === Long::class.java || type === java.lang.Long.TYPE) {
                    LongResponseBodyConverter.INSTANCE
                } else if (type === Short::class.java || type === java.lang.Short.TYPE) {
                    ShortResponseBodyConverter.INSTANCE
                } else {
                    GsonResponseBodyConverter<Any>(gson, type)
                }
            }
            // 指定某一接口自己的 App壳
            if (annotation is ApiData) {
                return PandaResponseBodyConverter<Any>(gson, type, annotation.clazz.java)
            }
        }
        return PandaResponseBodyConverter<Any>(gson, type)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return PandaRequestBodyConverter<Any>(gson, adapter)
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return super.stringConverter(type, annotations, retrofit)
    }

    companion object {
        fun create(): PandaConvertFactory {
            return create(gson())
        }

        private fun create(gson: Gson): PandaConvertFactory {
            return PandaConvertFactory(gson)
        }
    }
}