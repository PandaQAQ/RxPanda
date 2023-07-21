package com.pandaq.sample.entities.apidata

import com.google.gson.annotations.SerializedName
import com.pandaq.ktpanda.entity.IApiData

/**
 * Created by huxinyu on 2019/6/18.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
data class ZooApiData<T>(
    @SerializedName("errorCode") override val code: String,
    @SerializedName("errorMsg") override val msg: String,
    @SerializedName("response") override val data: T,
) : IApiData<T> {
    override fun isSuccess(): Boolean {
        return code.toInt() == 100
    }

}