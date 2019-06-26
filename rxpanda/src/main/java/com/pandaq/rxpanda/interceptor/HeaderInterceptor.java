package com.pandaq.rxpanda.interceptor;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :请求头拦截器，为每一次请求添加请求头
 */
public class HeaderInterceptor implements Interceptor {

    /**
     * 设置的 header
     */
    private Map<String, String> headers;

    public HeaderInterceptor(@NonNull Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (checkNotNull(headers)) {
            for (Map.Entry<String, String> item : headers.entrySet()) {
                builder.header(item.getKey(), item.getValue());
            }
        }
        return chain.proceed(builder.build());
    }

    private boolean checkNotNull(Map map) {
        return map != null && !map.isEmpty();
    }
}
