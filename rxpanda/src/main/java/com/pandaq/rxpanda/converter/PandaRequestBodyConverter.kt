package com.pandaq.rxpanda.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :请求 Body 解析类，解析 @Body 注解类
 */
class PandaRequestBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<out Any>
) : Converter<T, RequestBody> {
    
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value as Nothing?)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }
}