package com.pandaq.sample.entities

import java.io.Serializable

/**
 * Created by huxinyu on 2020/12/3.
 * Email : panda.h@foxmail.com
 * Description :
 */
data class UserTest(
    val avatar: String?,
    val birthday: String?,
    val healthStatus: Int?,
    val identityName: String?,
    val mobile: String?,
    val name: String?,
    val planStatus: Int?,
    val resultList: List<String>?,
    val sex: Int?,
    val status: Int?,
    val userId: String?
):Serializable