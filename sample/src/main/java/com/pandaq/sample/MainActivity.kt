package com.pandaq.sample

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pandaq.ktpanda.KtPanda
import com.pandaq.ktpanda.callbacks.DownloadCallBack
import com.pandaq.sample.apis.ApiService
import com.pandaq.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val apiService = KtPanda
        .retrofit()
        .create(ApiService::class.java)
    private val apiService1 = KtPanda
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
                    KtPanda.download("https://v-cdn.zjol.com.cn/276982.mp4")
                        .target("$path/sources", "aaa.mp4")
                        .request(object : DownloadCallBack() {
                            override fun onDone() {
                                Log.e("download", "下载资源成功了")
                            }

                            override fun onFail(e: Exception?) {
                                Log.e("download", "下载资源失败了")
                            }

                            override fun onProgress(progress: Int) {
                                Log.e("download", "下载资源 $progress%")
                            }

                        })
                }
            }

            R.id.intData -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = apiService.intData()
                        withContext(Dispatchers.Main) {
                            binding.dataString.text = "下载资源 $result"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "请求结束", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            R.id.userData -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = apiService.getUser()
                        withContext(Dispatchers.Main) {
                            binding.dataString.text = "${Gson().toJson(result)}"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "请求结束", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            R.id.mockdata -> {

            }
        }
    }
}
