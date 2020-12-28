package com.pandaq.sample

import android.app.Application
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.entity.NullDataValue
import com.pandaq.rxpanda.interceptor.HeaderInterceptor
import com.pandaq.rxpanda.log.HttpLoggingInterceptor

/**
 * Created by huxinyu on 2019/6/30.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val defValues = NullDataValue()
        defValues.defBoolean = false
        defValues.defDouble = -1.0
        defValues.defFloat = -1.0f
        defValues.defInt = -1
        defValues.defLong = -1L
        defValues.defString = "这是自动补全数据"

        RxPanda.globalConfig()
            .baseUrl("https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/")
            .trustAllHost(true)
            .defaultValue(defValues)
            .netInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addGlobalHeader("test", "testHeader")
            .apiSuccessCode(0L)
            .debug(BuildConfig.DEBUG)
    }
}