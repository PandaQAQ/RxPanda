package com.pandaq.rxpanda.interceptor;

import androidx.annotation.NonNull;
import com.pandaq.rxpanda.callbacks.DownloadCallBack;
import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.models.ProgressDownloadBody;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :下载拦截器
 */
public class DownloadInterceptor implements Interceptor {

    private TransmitCallback mCallback;

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
