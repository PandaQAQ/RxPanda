package com.pandaq.rxpanda.log

import android.util.Log
import com.pandaq.rxpanda.config.HttpGlobalConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects

/**
 * Created by huxinyu on 2019/6/13.
 * Email : panda.h@foxmail.com
 *
 *
 * Description :日志暂存对象
 */
internal class LogEntity {
    private val tag = "RxPanda"
    private var logs: MutableList<String>? = ArrayList()
    private val LINE_SEPARATOR = Objects.requireNonNull(System.getProperty("line.separator"))

    init {
        addLog(" ")
        addLog("╔════════════════════════  HTTP  START  ══════════════════════════")
        addLog("")
    }

    /**
     * 临时保存日志
     *
     * @param log 日志
     */
    fun addLog(log: String?) {
        if (!HttpGlobalConfig.instance.isDebug) return
        if (log == null) return
        if (log == " " || log.startsWith("{")
            || log.startsWith("╔") || log.startsWith("╚")
        ) {
            logs!!.add(log)
        } else {
            logs!!.add("║$log")
        }
    }

    /**
     * 输出日志到控制台
     */
    fun printLog() {
        if (!HttpGlobalConfig.instance.isDebug) return
        addLog("")
        addLog("╚════════════════════════  HTTP  END  ═══════════════════════════")
        addLog(" ")
        for (log in logs!!) {
            logJson(log)
        }
        logs!!.clear()
        logs = null
    }

    /**
     * 格式化 json 后输出日志（网络日志拦截器信息打印）
     *
     * @param msg 格式化前 json 数据
     */
    private fun logJson(msg: String) {
        var message: String
        try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                message = jsonObject.toString(2) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                message = jsonArray.toString(2)
            } else {
                message = msg
                if (HttpGlobalConfig.instance.isDebug) {
                    Log.d(tag, message)
                }
                return
            }
        } catch (e: JSONException) {
            message = msg
            if (HttpGlobalConfig.instance.isDebug) {
                Log.d(tag, message)
            }
            return
        }
        // 输出 json 格式数据
        printLine(true)
        message = LINE_SEPARATOR + message
        val lines =
            message.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            if (!line.isEmpty() && HttpGlobalConfig.instance.isDebug) {
                Log.d(tag, "║ $line")
            }
        }
        printLine(false)
    }

    /**
     * 打印日志分割线
     *
     * @param isTop 是否为顶部分割线
     */
    private fun printLine(isTop: Boolean) {
        if (isTop) {
            if (HttpGlobalConfig.instance.isDebug) {
                Log.d(tag, "║")
                Log.d(tag, "║——————————————————JSON START——————————————————")
            }
        } else {
            if (HttpGlobalConfig.instance.isDebug) {
                Log.d(tag, "║——————————————————JSON END———————————————————")
                Log.d(tag, "║")
            }
        }
    }
}