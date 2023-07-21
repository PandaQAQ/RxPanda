package com.pandaq.rxpanda

import android.util.Log
import kotlinx.coroutines.Job

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description : http requests manage
 */
class RequestJobHelper private constructor() {
    private val activeJobs: MutableMap<Any, Job> = HashMap()

    fun addJob(tag: Any, Job: Job) {

        if (activeJobs.containsKey(tag)) {
            Log.w("RxPanda", "the tag: $tag has been used by another request !!!")
        }
        activeJobs[tag] = Job
    }

    fun addJobs(tags: Map<Any, Job>) {
        for (key in tags.keys) {
            if (activeJobs.containsKey(key)) {
                Log.w("RxPanda", "the tag: $key has been used by another request !!!")
            }
        }
        activeJobs.putAll(tags)
    }

    /**
     * 移除指定 tag
     *
     * @param tag 被移除的 tag
     */
    fun removeJob(tag: Any) {
        activeJobs.remove(tag)
    }

    /**
     * 移除指定 tag
     *
     * @param tags 被移除的 tag
     */
    fun removeJobs(tags: ArrayList<Any>) {
        tags.forEach {
            removeJob(it)
        }
    }

    /**
     * 移除所有管理请求
     */
    private fun removeAll() {
        activeJobs.clear()
    }

    /**
     * 取消指定 tag
     *
     * @param tag 指定tag
     */
    fun cancelJob(tag: Any) {
        activeJobs.entries.forEach {
            if (it.key == tag && it.value.isActive) {
                it.value.cancel()
                activeJobs.remove(it.key)
            }
        }
    }

    /**
     * 根据 tag 取消网络回调
     *
     * @param tags 被取消的请求的 tag
     */
    fun cancelJobs(vararg tags: Any) {
        activeJobs.entries.forEach {
            if (it.key in tags && it.value.isActive) {
                it.value.cancel()
                activeJobs.remove(it.key)
            }
        }
    }

    /**
     * 取消所有已经加入管理的网络请求监听
     */
    fun cancelAll() {
        for ((_, job) in activeJobs) {
            if (job.isActive) {
                job.cancel()
            }
        }
        removeAll()
    }

    companion object {
        private var sManager: RequestJobHelper? = null

        @JvmStatic
        @Synchronized
        fun get(): RequestJobHelper? {
            if (sManager == null) {
                sManager = RequestJobHelper()
            }
            return sManager
        }
    }
}