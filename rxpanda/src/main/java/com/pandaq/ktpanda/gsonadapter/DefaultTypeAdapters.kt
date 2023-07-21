package com.pandaq.ktpanda.gsonadapter

import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.internal.LazilyParsedNumber
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.pandaq.ktpanda.config.HttpGlobalConfig
import java.io.IOException
import java.math.BigDecimal

/**
 * Created by huxinyu on 2019/7/19.
 * Email : panda.h@foxmail.com
 * Description :
 */
class DefaultTypeAdapters private constructor() {
    init {
        throw UnsupportedOperationException()
    }

    companion object {
        @JvmField
        val BOOLEAN: TypeAdapter<Boolean?> = object : TypeAdapter<Boolean?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Boolean {
                val peek = `in`.peek()
                if (peek == JsonToken.NULL) {
                    `in`.nextNull()
                    // 返回配置的默认值
                    return if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defBoolean
                    } else {
                        false
                    }
                } else if (peek == JsonToken.STRING) {
                    // support strings for compatibility with GSON 1.7
                    return java.lang.Boolean.parseBoolean(`in`.nextString())
                }
                return `in`.nextBoolean()
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, bol: Boolean?) {
                var value: Boolean? = bol
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defBoolean
                    } else {
                        false
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val BOOLEAN_AS_STRING: TypeAdapter<Boolean?> = object : TypeAdapter<Boolean?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Boolean {
                if (`in`.peek() == JsonToken.NULL) {
                    `in`.nextNull()
                    // 返回配置的默认值
                    return if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defBoolean
                    } else {
                        false
                    }
                }
                return if (`in`.peek() == JsonToken.BOOLEAN) {
                    `in`.nextBoolean()
                } else java.lang.Boolean.valueOf(`in`.nextString())
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, bol: Boolean?) {
                var value: Boolean? = bol
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defBoolean
                    } else {
                        false
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val INTEGER: TypeAdapter<Int?> = object : TypeAdapter<Int?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Int {
                return when (val jsonToken = `in`.peek()) {
                    JsonToken.NULL -> {
                        `in`.nextNull()
                        if (HttpGlobalConfig.instance.defValues != null) {
                            HttpGlobalConfig.instance.defValues!!.defInt
                        } else {
                            0
                        }
                    }

                    JsonToken.NUMBER -> `in`.nextInt()
                    JsonToken.STRING -> {
                        val str = `in`.nextString()
                        if (TextUtils.isEmpty(str)) {
                            return if (HttpGlobalConfig.instance.defValues != null) {
                                HttpGlobalConfig.instance.defValues!!.defInt
                            } else {
                                0
                            }
                        }
                        Integer.valueOf(str)
                    }

                    else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, int: Int?) {
                var value: Int? = int
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defInt
                    } else {
                        0
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val LONG: TypeAdapter<Long?> = object : TypeAdapter<Long?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Long {
                return when (val jsonToken = `in`.peek()) {
                    JsonToken.NULL -> {
                        `in`.nextNull()
                        if (HttpGlobalConfig.instance.defValues != null) {
                            HttpGlobalConfig.instance.defValues!!.defLong
                        } else {
                            0L
                        }
                    }

                    JsonToken.NUMBER -> `in`.nextLong()
                    JsonToken.STRING -> {
                        val str = `in`.nextString()
                        if (TextUtils.isEmpty(str)) {
                            return if (HttpGlobalConfig.instance.defValues != null) {
                                HttpGlobalConfig.instance.defValues!!.defLong
                            } else {
                                0L
                            }
                        }
                        java.lang.Long.valueOf(str)
                    }

                    else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, long: Long?) {
                var value: Long? = long
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defLong
                    } else {
                        0L
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val FLOAT: TypeAdapter<Float?> = object : TypeAdapter<Float?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Float {
                return when (val jsonToken = `in`.peek()) {
                    JsonToken.NULL -> {
                        `in`.nextNull()
                        if (HttpGlobalConfig.instance.defValues != null) {
                            HttpGlobalConfig.instance.defValues!!.defFloat
                        } else {
                            0.0f
                        }
                    }

                    JsonToken.NUMBER -> `in`.nextDouble().toFloat()
                    JsonToken.STRING -> {
                        val str = `in`.nextString()
                        if (TextUtils.isEmpty(str)) {
                            return if (HttpGlobalConfig.instance.defValues != null) {
                                HttpGlobalConfig.instance.defValues!!.defFloat
                            } else {
                                0.0f
                            }
                        }
                        java.lang.Float.valueOf(str)
                    }

                    else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, fl: Float?) {
                var value: Float? = fl
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defFloat
                    } else {
                        0.0f
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val DOUBLE: TypeAdapter<Double?> = object : TypeAdapter<Double?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Double {
                return when (val jsonToken = `in`.peek()) {
                    JsonToken.NULL -> {
                        `in`.nextNull()
                        if (HttpGlobalConfig.instance.defValues != null) {
                            HttpGlobalConfig.instance.defValues!!.defDouble
                        } else {
                            0.0
                        }
                    }

                    JsonToken.NUMBER -> `in`.nextDouble()
                    JsonToken.STRING -> {
                        val str = `in`.nextString()
                        if (TextUtils.isEmpty(str)) {
                            return if (HttpGlobalConfig.instance.defValues != null) {
                                HttpGlobalConfig.instance.defValues!!.defDouble
                            } else {
                                0.0
                            }
                        }
                        java.lang.Double.valueOf(str)
                    }

                    else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, db: Double?) {
                var value: Double? = db
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defDouble
                    } else {
                        0.0
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val NUMBER: TypeAdapter<Number?> = object : TypeAdapter<Number?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): Number {
                return when (val jsonToken = `in`.peek()) {
                    JsonToken.NULL -> {
                        `in`.nextNull()
                        if (HttpGlobalConfig.instance.defValues != null) {
                            HttpGlobalConfig.instance.defValues!!.defDouble
                        } else {
                            0
                        }
                    }

                    JsonToken.NUMBER, JsonToken.STRING -> {
                        val str = `in`.nextString()
                        if (TextUtils.isEmpty(str)) {
                            return if (HttpGlobalConfig.instance.defValues != null) {
                                HttpGlobalConfig.instance.defValues!!.defDouble
                            } else {
                                0
                            }
                        }
                        LazilyParsedNumber(str)
                    }

                    else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, num: Number?) {
                var value: Number? = num
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defDouble
                    } else {
                        0
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val STRING: TypeAdapter<String?> = object : TypeAdapter<String?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): String? {
                val peek = `in`.peek()
                if (peek == JsonToken.NULL) {
                    `in`.nextNull()
                    return if (HttpGlobalConfig.instance.defValues != null) {
                        HttpGlobalConfig.instance.defValues!!.defString!!
                    } else {
                        null
                    }
                }
                if (peek == JsonToken.NUMBER) {
                    val dbNum = `in`.nextDouble()
                    if (dbNum > Long.MAX_VALUE) {
                        return dbNum.toString()
                    }
                    // 如果是整数
                    return if (dbNum == dbNum.toLong().toDouble()) {
                        dbNum.toLong().toString()
                    } else {
                        dbNum.toString()
                    }
                }
                /* coerce booleans to strings for backwards compatibility */return if (peek == JsonToken.BOOLEAN) {
                    java.lang.Boolean.toString(`in`.nextBoolean())
                } else `in`.nextString()
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, str: String?) {
                var value: String? = str
                if (value == null) {
                    if (HttpGlobalConfig.instance.defValues != null) {
                        value = HttpGlobalConfig.instance.defValues!!.defString
                    }
                }
                out.value(value)
            }
        }
        @JvmField
        val BIG_DECIMAL: TypeAdapter<BigDecimal?> = object : TypeAdapter<BigDecimal?>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): BigDecimal {
                if (`in`.peek() == JsonToken.NULL) {
                    `in`.nextNull()
                    return if (HttpGlobalConfig.instance.defValues != null) {
                        BigDecimal(HttpGlobalConfig.instance.defValues!!.defInt)
                    } else {
                        BigDecimal(0)
                    }
                }
                return try {
                    BigDecimal(`in`.nextString())
                } catch (e: NumberFormatException) {
                    throw JsonSyntaxException(e)
                }
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, dcl: BigDecimal?) {
                var value: BigDecimal? = dcl
                if (value == null) {
                    value = if (HttpGlobalConfig.instance.defValues != null) {
                        BigDecimal(HttpGlobalConfig.instance.defValues!!.defInt)
                    } else {
                        BigDecimal(0)
                    }
                }
                out.value(value)
            }
        }
    }
}