package com.pandaq.rxpanda.constants

import okhttp3.MediaType

/**
 * Created by huxinyu on 2019/3/15.
 * Email : panda.h@foxmail.com
 * Description : http 请求参数类型
 */
object MediaTypes {
    @JvmField
    val APPLICATION_JSON_TYPE = MediaType.parse("application/json;charset=utf-8")
    @JvmField
    val APPLICATION_OCTET_STREAM_TYPE = MediaType.parse("application/octet-stream")
    @JvmField
    val APPLICATION_XML_TYPE = MediaType.parse("application/xml;charset=utf-8")
    @JvmField
    val TEXT_HTML_TYPE = MediaType.parse("text/html;charset=utf-8")
    @JvmField
    val TEXT_XML_TYPE = MediaType.parse("text/xml;charset=utf-8")
    @JvmField
    val TEXT_PLAIN_TYPE = MediaType.parse("text/plain;charset=utf-8")
    @JvmField
    val IMAGE_TYPE = MediaType.parse("image/*")
}