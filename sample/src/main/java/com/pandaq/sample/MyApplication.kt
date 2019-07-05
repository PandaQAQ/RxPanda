package com.pandaq.sample

import android.app.Application
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.interceptor.HttpLoggingInterceptor

/**
 * Created by huxinyu on 2019/6/30.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxPanda.globalConfig()
            .debug(BuildConfig.DEBUG)
            .baseUrl("https://www.panda.sss")
            .hosts("www.easy-mock.com")
            .netInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .apiSuccessCode(100L)
    }
}