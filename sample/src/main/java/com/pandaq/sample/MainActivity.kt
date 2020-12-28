package com.pandaq.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.gson.Gson
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.transformer.RxScheduler
import com.pandaq.rxpanda.utils.GsonUtil
import com.pandaq.sample.apis.ApiService
import com.pandaq.sample.apis.AppCallBack
import com.pandaq.sample.entities.User
import com.pandaq.sample.entities.ZooData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val apiService = RxPanda
        .retrofit()
        .connectTimeout(200)
        .create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stringData.setOnClickListener(this)
        intData.setOnClickListener(this)
        userData.setOnClickListener(this)
        userErrorType.setOnClickListener(this)
        this.javaClass.isAnonymousClass
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.stringData -> {
                apiService.stringData()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<String>() {
                        override fun success(data: String) {
                            dataString.text = data
                            dataString.setTextColor(Color.parseColor("#000000"))
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = "error:::$msg"
                            dataString.setTextColor(Color.parseColor("#ff0000"))
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.intData -> {
                apiService.intData()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<Int>() {
                        override fun success(data: Int) {
                            dataString.text = data.toString()
                            dataString.setTextColor(Color.parseColor("#000000"))
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = "error:::$msg"
                            dataString.setTextColor(Color.parseColor("#ff0000"))
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.userData -> {
                apiService.user
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<User>() {
                        override fun success(data: User) {
                            dataString.text = Gson().toJson(data)
                            dataString.setTextColor(Color.parseColor("#000000"))
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = "error:::$msg"
                            dataString.setTextColor(Color.parseColor("#ff0000"))
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.userErrorType -> {
                apiService.typeError()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.retrySync(10))
                    .subscribe(object : AppCallBack<User>() {
                        override fun success(data: User) {
                            dataString.text = GsonUtil.gson().toJson(data)
                            dataString.setTextColor(Color.parseColor("#000000"))
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = "error:::$msg"
                            dataString.setTextColor(Color.parseColor("#ff0000"))
                        }

                        override fun finish(success: Boolean) {

                        }

                    })

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
