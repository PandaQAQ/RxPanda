package com.pandaq.rxpanda.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.annotation.CacheIt;
import com.pandaq.rxpanda.annotation.IgnoreCache;
import com.pandaq.rxpanda.config.HttpGlobalConfig;
import com.pandaq.rxpanda.log.HttpLoggingInterceptor;

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
 * Description :是否使用缓存的拦截器，对添加了不需要缓存注解的接口和IO接口以外的所有接口操作
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // IO 相关的接口直接返回请求结果，不做缓存相关处理
        String ioFlag = request.header(HttpLoggingInterceptor.IO_FLAG_HEADER);
        boolean isIoRequest = !TextUtils.isEmpty(ioFlag);
        if (isIoRequest) {
            return chain.proceed(request);
        }

        //添加忽略缓存注解的接口直接返回请求结果，不做缓存相关处理
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            if (RxPanda.getConfig().cacheAll()) { //全局使用缓存
                Method method = invocation.method();
                IgnoreCache ignoreCache = method.getAnnotation(IgnoreCache.class);
                if (ignoreCache != null) { //忽略缓存，使用真实数据
                    return chain.proceed(request);
                } else {
                    // 返回缓存数据
                    return getCacheResponse(chain, request);
                }
            } else { //根据配置使用缓存
                Method method = invocation.method();
                CacheIt cacheIt = method.getAnnotation(CacheIt.class);
                if (cacheIt != null) {
                    // 返回缓存数据
                    return getCacheResponse(chain, request);
                } else {
                    return chain.proceed(request);
                }
            }
        } else {
            return chain.proceed(request);
        }
    }

    /**
     * 构建缓存请求
     *
     * @param chain   请求的 chain
     * @param request 请求体
     * @return 结果
     * @throws IOException 异常
     */
    private Response getCacheResponse(Chain chain, Request request) throws IOException {
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
