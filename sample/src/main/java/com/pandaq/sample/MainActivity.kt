package com.pandaq.sample

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pandaq.rxpanda.RxPanda
import com.pandaq.rxpanda.callbacks.DownloadCallBack
import com.pandaq.rxpanda.callbacks.UploadCallBack
import com.pandaq.rxpanda.exception.ApiException
import com.pandaq.sample.apis.ApiService
import com.pandaq.sample.databinding.ActivityMainBinding
import com.pandaq.sample.entities.UserTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val apiService = RxPanda
        .retrofit()
        .create(ApiService::class.java)
    private val apiService1 = RxPanda
        .retrofit()
        .connectTimeout(200)
        .create(ApiService::class.java)

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stringData.setOnClickListener(this)
        binding.intData.setOnClickListener(this)
        binding.userData.setOnClickListener(this)
        binding.mockdata.setOnClickListener(this)
        this.javaClass.isAnonymousClass
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.stringData -> {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                CoroutineScope(Dispatchers.IO).launch {
                    RxPanda.download("https://v-cdn.zjol.com.cn/276982.mp4")
                        .target("$path/sources", "aaa.mp4")
                        .request(object : DownloadCallBack() {
                            override fun onDone() {
                                Log.e("download", "下载资源成功了")
                            }

                            override fun onFail(e: Exception?) {
                                Log.e("download", "下载资源失败了")
                            }

                            override fun onProgress(progress: Int) {
                                binding.dataString.text = "下载资源 $progress%"
                                Log.e("download", "下载资源 $progress%")
                            }

                        })
                }
            }

            R.id.intData -> {

            }

            R.id.userData -> {

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
                CoroutineScope(Dispatchers.IO)
                    .launch {
                        val result = RxPanda.post("https://www.baidu.com")
                            .mockData(Constants.MOCK_DATA)
                            .request(UserTest::class.java)
                        result
                    }

//                object : AppCallBack<ApiData<List<UserTest>>>() {
//                    override fun success(data: ApiData<List<UserTest>>) {
//                        binding.dataString.text = GsonUtil.gson().toJson(data)
//                        binding.dataString.setTextColor(Color.parseColor("#000000"))
//                    }
//
//                    @SuppressLint("SetTextI18n")
//                    override fun fail(code: String?, msg: String?) {
//                        binding.dataString.text = "error:::$msg"
//                        binding.dataString.setTextColor(Color.parseColor("#ff0000"))
//                    }
//
//                    override fun finish(success: Boolean) {
//
//                    }
//
//                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
