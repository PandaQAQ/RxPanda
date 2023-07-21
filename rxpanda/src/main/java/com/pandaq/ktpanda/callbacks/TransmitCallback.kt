package com.pandaq.ktpanda.callbacks

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 *
 *
 * Description : 上传和下载请求回调
 */
interface TransmitCallback {
    /**
     * 读写完成
     */
    fun done()

    /**
     * 上传/下载失败
     *
     * @param e 异常类
     */
    fun failed(e: Exception?)

    /**
     * 上传/下载进度
     *
     * @param progress 进度 % 单位
     */
    fun progress(progress: Int)
}