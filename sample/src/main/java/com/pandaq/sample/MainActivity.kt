package com.pandaq.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.pandaq.app_launcher.entites.ZhihuData
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.callbacks.DownloadCallBack
import com.pandaq.rxpanda.callbacks.UploadCallBack
import com.pandaq.rxpanda.transformer.RxScheduler
import com.pandaq.rxpanda.utils.GsonUtil
import com.pandaq.sample.apis.ApiService
import com.pandaq.sample.apis.AppCallBack
import com.pandaq.sample.entities.ZooData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val apiService = RxPanda
        .retrofit()
        .create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        normalData.setOnClickListener(this)
        newJsonKeyData.setOnClickListener(this)
        stringData.setOnClickListener(this)
        noShellData.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.normalData -> {
//                apiService.zooList
//                    .doOnSubscribe { t -> compositeDisposable.add(t) }
//                    .compose(RxScheduler.sync())
//                    .subscribe(object : AppCallBack<List<ZooData>>() {
//                        override fun success(data: List<ZooData>?) {
//                            dataString.text = GsonUtil.gson().toJson(data)
//                        }
//
//                        override fun fail(code: Long?, msg: String?) {
//                            dataString.text = msg
//                        }
//
//                        override fun finish(success: Boolean) {
//
//                        }
//
//                    })

//                val target = filesDir.absolutePath + "download"
//                RxPanda.download(
//                    "http://www.shijieditu.net/ditu/allimg/170730/2254393013-0.jpg"
//                )
//                    .target(target, "worldmap.jpg")
//                    .request(object : DownloadCallBack() {
//                        override fun done(success: Boolean) {
//                            dataString.text = "done and download success :$success"
//                        }
//
//                        override fun onFailed(e: Exception) {
//                            Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
//                        }
//
//                        override fun inProgress(progress: Int) {
//                            dataString.text = "$progress"
//                            dataString.setBackgroundColor(Color.parseColor("#aa00ff"))
//                        }
//
//                    })
                val file = File(filesDir.absolutePath + "download/worldmap.jpg")
                RxPanda.upload("http://192.168.0.34:8080/up")
                    .addHeader("Content-Disposition","attachment;filename=FileName.jpeg")
                    .addImageFile("map", file)
                    .request(object : UploadCallBack() {
                        override fun done(success: Boolean) {
                            dataString.text = "done and download success :$success"
                        }

                        override fun onFailed(e: Exception) {
                            Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                        }

                        override fun inProgress(progress: Int) {
                            dataString.text = "$progress"
                            dataString.setBackgroundColor(Color.parseColor("#aa00ff"))
                        }

                    })
            }

            R.id.newJsonKeyData -> {
                apiService.newJsonKeyData()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<List<ZooData>>() {
                        override fun success(data: List<ZooData>?) {
                            dataString.text = GsonUtil.gson().toJson(data)
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = msg
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.stringData -> {
                apiService.stringData()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<String>() {
                        override fun success(data: String) {
                            dataString.text = GsonUtil.gson().toJson(data)
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = msg
                        }

                        override fun finish(success: Boolean) {

                        }

                    })
            }

            R.id.noShellData -> {
                apiService.zhihu()
                    .doOnSubscribe { t -> compositeDisposable.add(t) }
                    .compose(RxScheduler.sync())
                    .subscribe(object : AppCallBack<ZhihuData>() {
                        override fun success(data: ZhihuData) {
                            dataString.text = GsonUtil.gson().toJson(data)
                        }

                        override fun fail(code: Long?, msg: String?) {
                            dataString.text = msg
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
