package com.pandaq.rxpanda.requests.okhttp.io

import android.util.Log
import com.pandaq.rxpanda.callbacks.DownloadCallBack
import com.pandaq.rxpanda.interceptor.DownloadInterceptor
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :
 */
class DownloadRequest(url: String) : IORequest<DownloadRequest>(url) {

    private var targetFile: File? = null
    fun target(dirName: String, fileName: String): DownloadRequest {
        val file = File(dirName)
        if (!file.exists()) {
            val result = file.mkdirs()
            if (!result) {
                Log.e("DownloadRequest :", "mkdirs failed !")
            }
        }
        targetFile = File(dirName + File.separator + fileName)
        if (!targetFile!!.exists()) {
            try {
                val res = targetFile!!.createNewFile()
                if (!res) {
                    Log.e("DownloadRequest :", "createNewFile failed !")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return this
    }

    fun target(file: File?): DownloadRequest {
        targetFile = file
        return this
    }

    override suspend fun execute(): ResponseBody? {
        return mApi?.downFile(url, localParams)
    }

    suspend fun request(callback: DownloadCallBack?) {
        if (targetFile == null || !targetFile!!.exists()) {
            Log.d("DownloadRequest", "targeFile not found !!!")
        }
        targetFile?.let {
            callback?.setTargetFile(it)
        }
        netInterceptor(DownloadInterceptor(callback))
        injectLocalParams()
        execute()
    }
}