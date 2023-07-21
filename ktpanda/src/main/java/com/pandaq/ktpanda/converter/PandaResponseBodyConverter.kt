package com.pandaq.ktpanda.converter

import android.util.Log
import com.google.gson.Gson
import com.pandaq.ktpanda.HttpCode
import com.pandaq.ktpanda.config.HttpGlobalConfig
import com.pandaq.ktpanda.entity.EmptyData
import com.pandaq.ktpanda.entity.IApiData
import com.pandaq.ktpanda.exception.ApiException
import com.pandaq.ktpanda.exception.ExceptionType
import com.pandaq.ktpanda.utils.GsonUtil.gson
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.math.BigDecimal

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :响应实体解析类,对返回数据实体做去壳处理
 */
class PandaResponseBodyConverter<T> : Converter<ResponseBody, T> {
    private var gson: Gson
    //定义的解析类型
    private var apiDataClazz: Class<*>? = null
    private var dataType: Type?
    internal constructor(gson: Gson, dataType: Type?) {
        this.gson = gson
        this.dataType = dataType
    }

    internal constructor(gson: Gson, dataType: Type?, clazz: Class<out IApiData<*>?>?) {
        this.gson = gson
        this.dataType = dataType
        apiDataClazz = clazz
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        if (apiDataClazz == null) {
            apiDataClazz = HttpGlobalConfig.instance.getApiDataClazz()
        }
        val apiData = gson.fromJson<IApiData<T?>>(response, apiDataClazz as Type?)
        /* 如是按约定格式返回数据 apiData 中的 code 是必须的。
         * 因此可以用 code 是否存在来判断数据是否合法
         */return if (apiData.code == null) {
            throw ApiException(HttpCode.FRAMEWORK.SHELL_FORMAT_ERROR, response, response)
        } else {
            val data = if (apiData.data == null) defaultData() else gson().toJson(apiData.data)
            if (!apiData.isSuccess()) {
                val exception = ApiException(apiData.code!!, apiData.msg, data)
                exception.exceptionType = ExceptionType.API
                throw exception
            } else {
                try {
                    gson().fromJson<T>(data, dataType)
                } catch (e: Exception) {
                    if (HttpGlobalConfig.instance.isDebug) {
                        e.printStackTrace()
                        Log.w("errorData: ", response)
                    }
                    // 原始数据解析不通
                    val exception = ApiException(apiData.code!!, "接口数据类型不匹配", data)
                    exception.exceptionType = ExceptionType.JSON_PARSE
                    throw exception
                } finally {
                    value.close()
                }
            }
        }
    }

    /**
     * 与 DefaultTypeAdapter 配合，处理 data 为 null 或 data 未返回的情况下的数据
     *
     * @return 处理后的 data 填充值
     */
    private fun defaultData(): String {
        // 当解析类型为 DefaultTypeAdapter 中的类型时，设置为 null 交给 GsonAdapter 处理对应的空值
        val data: String = if (Boolean::class.java == dataType || Byte::class.java == dataType || Short::class.java == dataType || Int::class.java == dataType || Long::class.java == dataType || Float::class.java == dataType || Double::class.java == dataType || Number::class.java == dataType || String::class.java == dataType || BigDecimal::class.java == dataType || StringBuilder::class.java == dataType || StringBuffer::class.java == dataType) {
                "null"
            } else if (MutableList::class.java.name == clazzName) { //解析类型为 List 时返回一个空集合的 gson 数据
                gson().toJson(ArrayList<Any>())
            } else {  //为 null 时默认按空对象解析
                gson().toJson(EmptyData())
            }
        return data
    }

    /**
     * 获取解析 data 对象的完整类名
     *
     * @return 完整类名
     */
    private val clazzName: String?
        get() {
            var className: String? = null
            if (null != dataType) {
                className = when (dataType) {
                    is ParameterizedType -> {
                        val pt = dataType as ParameterizedType
                        val clz = pt.rawType as Class<*>
                        clz.name
                    }

                    is TypeVariable<*> -> {
                        val tType = dataType as TypeVariable<*>
                        tType.genericDeclaration.toString()
                    }

                    else -> {
                        val clz = dataType as Class<*>
                        clz.name
                    }
                }
            }
            return className
        }
}