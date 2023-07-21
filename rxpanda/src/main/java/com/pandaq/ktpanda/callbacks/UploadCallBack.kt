package com.pandaq.ktpanda.callbacks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
abstract class UploadCallBack : TransmitCallback {

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun done() {
        mainScope.launch {
            onDone()
        }
        mainScope.cancel()
    }

    override fun failed(e: Exception?) {
        mainScope.launch {
            onFail(e)
        }
    }

    override fun progress(progress: Int) {
        mainScope.launch {
            onProgress(progress)
        }
    }

    abstract fun onDone()

    abstract fun onFail(e: Exception?)

    abstract fun onProgress(progress: Int)
}