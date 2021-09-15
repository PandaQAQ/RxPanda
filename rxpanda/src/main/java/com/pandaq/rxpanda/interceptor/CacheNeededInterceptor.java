package com.pandaq.rxpanda.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.pandaq.rxpanda.annotation.CacheIt;
import com.pandaq.rxpanda.config.HttpGlobalConfig;

import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * Created by huxinyu on 2021/9/15.
 * Email : panda.h@foxmail.com
 * Description :是否使用缓存的拦截器，只对添加了需要缓存注解的接口进行缓存操作
 */
public class CacheNeededInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean needCache = false;
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            Method method = invocation.method();
            CacheIt cacheIt = method.getAnnotation(CacheIt.class);
            needCache = cacheIt != null;
        }
        if (!needCache) {
            return chain.proceed(request);
        }
        // 一般接口，进行缓存策略处理
        if (!isNetworkConnected()) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            return chain.proceed(request);
        }
        Response response = chain.proceed(request);
        if (response.code() != 200) {
            return response.newBuilder()
                    .header("Cache-Control", CacheControl.FORCE_CACHE.toString()).build();
        }
        return response;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) HttpGlobalConfig.getInstance().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network allNetwork : manager.getAllNetworks()) {
            NetworkInfo info = manager.getNetworkInfo(allNetwork);
            if (info.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
