package com.pandaq.rxpanda.entity

/**
 * Created by huxinyu on 2020/3/24.
 * Email : panda.h@foxmail.com
 * Description :data 为 null 或 gson 解析时的默认值对象
 */
class NullDataValue {
    @JvmField
    var defInt = 0
    @JvmField
    var defString: String? = null
    @JvmField
    var defFloat = 0f
    @JvmField
    var defDouble = 0.0
    @JvmField
    var defLong: Long = 0
    @JvmField
    var defBoolean = false
}