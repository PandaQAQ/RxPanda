package com.pandaq.rxpanda.models

import com.pandaq.rxpanda.callbacks.TransmitCallback
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import okio.Sink
import java.io.IOException

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 *
 *
 * Description : 文件上传进度监听扩展
 */
class ProgressUploadBody(
    //实际的待包装请求体
    private val requestBody: RequestBody?,
    //进度回调接口
    private val mCallback: TransmitCallback?
) : RequestBody() {
    //包装完成的BufferedSink
    private var bufferedSink: BufferedSink? = null

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    override fun contentType(): MediaType? {
        return requestBody?.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody?.contentLength() ?: 0
    }

    /**
     * 重写进行写入
     *
     * @param buffSink BufferedSink
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun writeTo(buffSink: BufferedSink) {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(buffSink))
        }
        //写入
        bufferedSink?.let { requestBody?.writeTo(it) }
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink?.flush()
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L

            //总字节长度，避免多次调用contentLength()方法
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                //回调
                sendStatus(bytesWritten, contentLength)
            }
        }
    }

    private fun sendStatus(bytesWritten: Long, contentLength: Long) {
        if (mCallback != null) {
            val progress = (bytesWritten * 1f / contentLength * 100).toInt()
            mCallback.progress(progress)
        }
    }
}