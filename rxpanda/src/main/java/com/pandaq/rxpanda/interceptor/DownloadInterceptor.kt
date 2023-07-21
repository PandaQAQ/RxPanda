package com.pandaq.rxpanda.interceptor;

import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.models.ProgressDownloadBody;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :下载拦截器
 */
public class DownloadInterceptor implements Interceptor {

    private final TransmitCallback mCallback;

    public DownloadInterceptor(TransmitCallback callback) {
        mCallback = callback;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        ProgressDownloadBody responseBody = new ProgressDownloadBody(response.body(), mCallback);
        return response.newBuilder()
                .body(responseBody)
                .build();
    }
}
