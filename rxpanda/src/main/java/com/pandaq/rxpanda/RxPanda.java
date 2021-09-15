package com.pandaq.rxpanda;

import android.content.Context;

import com.pandaq.rxpanda.config.HttpGlobalConfig;
import com.pandaq.rxpanda.requests.okhttp.GetRequest;
import com.pandaq.rxpanda.requests.okhttp.io.DownloadRequest;
import com.pandaq.rxpanda.requests.okhttp.io.UploadRequest;
import com.pandaq.rxpanda.requests.okhttp.post.PostBodyRequest;
import com.pandaq.rxpanda.requests.okhttp.post.PostFormRequest;
import com.pandaq.rxpanda.requests.okhttp.post.PostRequest;
import com.pandaq.rxpanda.requests.retrofit.RetrofitRequest;

/**
 * Created by huxinyu on 2019/1/9.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :http request tool class
 */
public class RxPanda {

    private RxPanda() {

    }

    public static HttpGlobalConfig init(Context context) {
        HttpGlobalConfig.getInstance().setContext(context.getApplicationContext());
        return HttpGlobalConfig.getInstance();
    }

    public static HttpGlobalConfig getConfig() {
        if (HttpGlobalConfig.getInstance().getContext() == null) {
            throw new RuntimeException("must call init(application) first!");
        }
        return HttpGlobalConfig.getInstance();
    }

    /**
     * normal get request
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /**
     * normal post request
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    /**
     * form post request
     */
    public static PostFormRequest postForm(String url) {
        return new PostFormRequest(url);
    }

    /**
     * body post request
     */
    public static PostBodyRequest postBody(String url) {
        return new PostBodyRequest(url);
    }


    /**
     * download request
     *
     * @param url download url
     */
    public static DownloadRequest download(String url) {
        return new DownloadRequest(url);
    }

    /**
     * 上传文件
     *
     * @param url 上传地址
     * @return return a UploadRequest object
     */
    public static UploadRequest upload(String url) {
        return new UploadRequest(url);
    }

    /**
     * retrofit request
     *
     * @return return a RetrofitRequest object
     */
    public static RetrofitRequest retrofit() {
        return new RetrofitRequest();
    }

    /**
     * 获取到请求管理器
     *
     * @return requestManager
     */
    public static RequestManager manager() {
        return RequestManager.get();
    }
}
