package com.pandaq.rxpanda.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * Created by huxinyu on 2019/6/25.
 * Email : panda.h@foxmail.com
 * Description :
 */
internal class ScalarResponseBodyConverters private constructor() {
    internal class ShortResponseBodyConverter : Converter<ResponseBody, Short> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Short {
            return value.string().toShort()
        }

        companion object {
            val INSTANCE = ShortResponseBodyConverter()
        }
    }

    internal class LongResponseBodyConverter : Converter<ResponseBody, Long> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Long {
            return java.lang.Long.valueOf(value.string())
        }

        companion object {
            val INSTANCE = LongResponseBodyConverter()
        }
    }

    internal class IntegerResponseBodyConverter : Converter<ResponseBody, Int> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Int {
            return Integer.valueOf(value.string())
        }

        companion object {
            val INSTANCE = IntegerResponseBodyConverter()
        }
    }

    internal class FloatResponseBodyConverter : Converter<ResponseBody, Float> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Float {
            return java.lang.Float.valueOf(value.string())
        }

        companion object {
            val INSTANCE = FloatResponseBodyConverter()
        }
    }

    internal class DoubleResponseBodyConverter : Converter<ResponseBody, Double> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Double {
            return java.lang.Double.valueOf(value.string())
        }

        companion object {
            val INSTANCE = DoubleResponseBodyConverter()
        }
    }

    internal class CharacterResponseBodyConverter : Converter<ResponseBody, Char> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Char {
            val body = value.string()
            return if (body.length != 1) {
                throw IOException("Expected body of length 1 for Character conversion but was " + body.length)
            } else {
                body[0]
            }
        }

        companion object {
            val INSTANCE = CharacterResponseBodyConverter()
        }
    }

    internal class ByteResponseBodyConverter : Converter<ResponseBody, Byte> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Byte {
            return java.lang.Byte.valueOf(value.string())
        }

        companion object {
            val INSTANCE = ByteResponseBodyConverter()
        }
    }

    internal class BooleanResponseBodyConverter : Converter<ResponseBody, Boolean> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Boolean {
            return java.lang.Boolean.valueOf(value.string())
        }

        companion object {
            val INSTANCE = BooleanResponseBodyConverter()
        }
    }

    internal class StringResponseBodyConverter : Converter<ResponseBody, String> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String {
            return value.string()
        }

        companion object {
            val INSTANCE = StringResponseBodyConverter()
        }
    }
}