package com.pandaq.ktpanda.models

import com.pandaq.ktpanda.callbacks.DownloadCallBack
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :待写入进度的响应体
 */
class ProgressDownloadBody(
    private val responseBody: ResponseBody?,
    private val mCallback: DownloadCallBack?
) : ResponseBody() {
    //包装完成的BufferedSource
    private var bufferedSource: BufferedSource? = null

    //记录当前的Progress
    private var currentProgress = 0

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    override fun contentType(): MediaType? {
        return responseBody?.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     */
    override fun contentLength(): Long {
        return responseBody?.contentLength() ?: 0
    }

    /**
     * 重新进行包装source
     *
     * @return BufferedSource
     */
    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            //包装
            bufferedSource = responseBody?.let { source(it.source()) }?.let { Okio.buffer(it) }
        }
        bufferedSource?.let {
            saveFile(it.inputStream())
        }
        return bufferedSource!!
    }

    /**
     * 写入文件到 SD 卡
     */
    private fun saveFile(inputStream: InputStream) {
        if (mCallback?.getTargetFile() == null) {
            mCallback?.failed(IOException())
            return
        }
        val fos = FileOutputStream(mCallback.getTargetFile())
        try {
            var len: Int
            val buffer = ByteArray(2048)
            while (-1 != inputStream.read(buffer).also { len = it }) {
                fos.write(buffer, 0, len)
            }
        } catch (e: IOException) {
            mCallback.failed(IOException())
            return
        } finally {
            try {
                fos.flush()
                fos.close()
                inputStream.close()
            } catch (e: Exception) {
                mCallback.failed(IOException())
            }
        }
        mCallback.done()
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            //当前读取字节数
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //回调，如果contentLength()不知道长度，会返回-1
                sendStatus(totalBytesRead)
                return bytesRead
            }
        }
    }

    private fun sendStatus(totalBytesRead: Long) {
        if (mCallback != null) {
            val progress = (totalBytesRead * 1f / contentLength() * 100).toInt()
            if (currentProgress != progress) {
                currentProgress = progress
                mCallback.progress(progress)
            }
        }
    }
}