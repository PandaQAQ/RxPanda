package com.pandaq.sample

import android.app.Application
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.log.HttpLoggingInterceptor
import com.pandaq.sample.entities.apidata.ZooApiData

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
            .baseUrl("https://www.easy-mock.com/mock/5cef4b3e651e4075bad237f8/example/")
            .hosts("www.easy-mock.com")
            .allowAllHost(true)
            .netInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .apiSuccessCode(100L)
            .debug(BuildConfig.DEBUG)
    }
}