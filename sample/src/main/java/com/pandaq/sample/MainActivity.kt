package com.pandaq.sample

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.callbacks.DownloadCallBack
import com.pandaq.rxpanda.entity.ApiData
import com.pandaq.rxpanda.transformer.RxScheduler
import com.pandaq.rxpanda.utils.GsonUtil
import com.pandaq.sample.apis.ApiService
import com.pandaq.sample.apis.AppCallBack
import com.pandaq.sample.entities.User
import com.pandaq.sample.entities.UserTest
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val apiService = RxPanda
        .retrofit()
        .create(ApiService::class.java)
    private val apiService1 = RxPanda
        .retrofit()
        .connectTimeout(200)
        .create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stringData.setOnClickListener(this)
        intData.setOnClickListener(this)
        userData.setOnClickListener(this)
        mockdata.setOnClickListener(this)
        this.javaClass.isAnonymousClass
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.stringData -> {
                RxPanda.download("https://v-cdn.zjol.com.cn/276982.mp4")
                    .target(filesDir.absolutePath + "/sources", "aaa.mp4")
                    .request(object : DownloadCallBack() {
                        override fun done(success: Boolean) {
                            Log.e("download","下载资源成功了")
                        }

                        override fun onFailed(exception: Exception?) {
                            // 进行错误上报
                            Log.e("download","下载资源失败了")
                        }

                        override fun inProgress(process: Int) {
                            Log.e("download","下载资源 $process%")
                        }

                    })
//                apiService.stringData()
//                    .doOnSubscribe { t -> compositeDisposable.add(t) }
//                    .compose(RxScheduler.sync())
//                    .subscribe(object : AppCallBack<Any>() {
//                        override fun success(data: Any) {
////                            dataString.text = data
//                            dataString.setTextColor(Color.parseColor("#000000"))
//                        }
//
//                        @SuppressLint("SetTextI18n")
//                        override fun fail(code: String?, msg: String?) {
//                            dataString.text = "error:::$msg"
//                            dataString.setTextColor(Color.parseColor("#ff0000"))
//                        }
//
//                        override fun finish(success: Boolean) {
//
//                        }
//
//                    })
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

                        @SuppressLint("SetTextI18n")
                        override fun fail(code: String?, msg: String?) {
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

                        @SuppressLint("SetTextI18n")
                        override fun fail(code: String?, msg: String?) {
                            dataString.text = "error:::$msg"
                            dataString.setTextColor(Color.parseColor("#ff0000"))
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.mockdata -> {
//                apiService.typeError("hah")
//                    .doOnSubscribe { t -> compositeDisposable.add(t) }
//                    .compose(RxScheduler.retrySync(10))
//                    .subscribe(object : AppCallBack<List<UserTest>>() {
//                        override fun success(data: List<UserTest>) {
//                            dataString.text = GsonUtil.gson().toJson(data)
//                            dataString.setTextColor(Color.parseColor("#000000"))
//                        }
//
//                        override fun fail(code: Long?, msg: String?) {
//                            dataString.text = "error:::$msg"
//                            dataString.setTextColor(Color.parseColor("#ff0000"))
//                        }
//
//                        override fun finish(success: Boolean) {
//
//                        }
//
//                    })
                RxPanda.post("https://www.baidu.com")
                    .mockData(Constants.MOCK_DATA)
                    .request(
                        object : AppCallBack<ApiData<List<UserTest>>>() {
                            override fun success(data: ApiData<List<UserTest>>) {
                                dataString.text = GsonUtil.gson().toJson(data)
                                dataString.setTextColor(Color.parseColor("#000000"))
                            }

                            @SuppressLint("SetTextI18n")
                            override fun fail(code: String?, msg: String?) {
                                dataString.text = "error:::$msg"
                                dataString.setTextColor(Color.parseColor("#ff0000"))
                            }

                            override fun finish(success: Boolean) {

                            }

                        }
                    )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
