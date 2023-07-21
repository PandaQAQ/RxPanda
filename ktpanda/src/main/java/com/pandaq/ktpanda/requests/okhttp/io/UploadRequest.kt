package com.pandaq.ktpanda.requests.okhttp.io

import com.pandaq.ktpanda.callbacks.UploadCallBack
import com.pandaq.ktpanda.constants.MediaTypes
import com.pandaq.ktpanda.interceptor.UploadInterceptor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :
 */
open class UploadRequest(suffixUrl: String) : IORequest<UploadRequest>(suffixUrl) {
    private val multipartBodyParts: MutableList<MultipartBody.Part?> = ArrayList()
    private val stringBuilder = StringBuilder()

    override suspend fun execute():ResponseBody? {
        if (stringBuilder.isNotEmpty()) {
            url += stringBuilder.toString()
        }
        if (localParams.isNotEmpty()) {
            val entryIterator: Iterator<Map.Entry<String, String>> = localParams.entries.iterator()
            var entry: Map.Entry<String, String>
            while (entryIterator.hasNext()) {
                entry = entryIterator.next()
                multipartBodyParts.add(
                    MultipartBody.Part.createFormData(
                        entry.key,
                        entry.value
                    )
                )
            }
        }
       return mApi?.uploadFiles(url, multipartBodyParts)
    }

    fun addUrlParam(paramKey: String?, paramValue: String?): UploadRequest {
        if (paramKey != null && paramValue != null) {
            if (stringBuilder.isEmpty()) {
                stringBuilder.append("?")
            } else {
                stringBuilder.append("&")
            }
            stringBuilder.append(paramKey).append("=").append(paramValue)
        }
        return this
    }

    fun addFiles(fileMap: Map<String?, File?>?): UploadRequest {
        if (fileMap == null) {
            return this
        }
        for ((key, value) in fileMap) {
            addFile(key, value)
        }
        return this
    }

    fun addFile(key: String?, file: File?): UploadRequest {
        if (key == null || file == null) {
            return this
        }
        val requestBody: RequestBody =
            RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file)
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData(key, file.name, requestBody)
        multipartBodyParts.add(part)
        return this
    }

    fun addImageFile(key: String?, file: File?): UploadRequest {
        if (key == null || file == null) {
            return this
        }
        val requestBody: RequestBody = RequestBody.create(MediaTypes.IMAGE_TYPE, file)
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData(key, file.name, requestBody)
        multipartBodyParts.add(part)
        return this
    }

    fun addBytes(key: String?, bytes: ByteArray?, name: String?): UploadRequest {
        if (key == null || bytes == null || name == null) {
            return this
        }
        val requestBody: RequestBody =
            RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, bytes)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData(key, name, requestBody)
        multipartBodyParts.add(part)
        return this
    }

    fun addStream(key: String?, inputStream: InputStream?, name: String?): UploadRequest {
        if (key == null || inputStream == null || name == null) {
            return this
        }
        val requestBody: RequestBody = create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, inputStream)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData(key, name, requestBody)
        multipartBodyParts.add(part)
        return this
    }

    protected fun create(mediaType: MediaType?, inputStream: InputStream): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return mediaType
            }

            override fun contentLength(): Long {
                return try {
                    inputStream.available().toLong()
                } catch (e: IOException) {
                    0
                }
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = Okio.source(inputStream)
                    source?.let { sink.writeAll(it) }
                } finally {
                    Util.closeQuietly(source)
                }
            }
        }
    }

    suspend fun request(callback: UploadCallBack?) {
        netInterceptor(UploadInterceptor(callback))
        injectLocalParams()
        execute()
    }
}