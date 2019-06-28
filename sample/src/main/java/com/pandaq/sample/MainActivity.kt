package com.pandaq.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.exception.ApiException
import com.pandaq.rxpanda.observer.ApiObserver
import com.pandaq.rxpanda.transformer.RxScheduler
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.just("data")
            .map {
                Log.d("RxJava", "start==>" + Thread.currentThread().name)
                return@map it
            }
            .subscribeOn(Schedulers.io())
            .map {
                Log.d("RxJava", "observeOn 1==>" + Thread.currentThread().name)
                return@map it
            }
//            .compose(RxScheduler.sync())
            .subscribeOn(Schedulers.computation())
            .map {
                Log.d("RxJava", "subscribeOn 2==>" + Thread.currentThread().name)
                return@map it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Log.d("RxJava", "observeOn 1==>" + Thread.currentThread().name)
                return@map it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ApiObserver<String>() {
                override fun onSuccess(data: String?) {
                    Log.d("RxJava", "subscribe ==>" + Thread.currentThread().name)
                }

                override fun onError(e: ApiException?) {

                }

                override fun finished(success: Boolean) {

                }

            })
    }
}
