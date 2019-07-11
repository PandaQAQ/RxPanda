package com.pandaq.rxpanda.interceptor;

import androidx.annotation.NonNull;
import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.callbacks.UploadCallBack;
import com.pandaq.rxpanda.models.ProgressUploadBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :进度监听拦截器
 */
public class UploadInterceptor implements Interceptor {

    private TransmitCallback mCallback;

    public UploadInterceptor(@NonNull TransmitCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        ProgressUploadBody requestBody = new ProgressUploadBody(request.body(), mCallback);
        Request finalRequest = request.newBuilder()
                .addHeader("Connection", "alive")
                .method(request.method(), requestBody)
                .build();
        return chain.proceed(finalRequest);
    }
}
