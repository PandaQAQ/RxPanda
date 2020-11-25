package com.pandaq.rxpanda.requests.okhttp.io;

import android.text.TextUtils;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.api.Api;
import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.requests.Request;
import com.pandaq.rxpanda.utils.CastUtils;
import io.reactivex.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.pandaq.rxpanda.log.HttpLoggingInterceptor.IO_FLAG_HEADER;

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
public abstract class IORequest<R extends IORequest<R>> extends Request<R> {

    // http api,兼容 rxJava 观察者模式，需要返回观察对象时，将请求转换成 Retrofit 去请求
    protected Api mApi;
    protected String url = "";
    // request tag
    protected Object tag;
    protected Map<String, String> localParams = new LinkedHashMap<>();//请求参数
    private long retryDelayMillis;//请求失败重试间隔时间

    /**
     * set request‘s tag，use to manage the request
     *
     * @param tag request's tag
     * @return Request Object
     */
    public R tag(@NonNull Object tag) {
        this.tag = tag;
        return CastUtils.cast(this);
    }

    public IORequest(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.url = url;
        }
        addHeader(IO_FLAG_HEADER, "IORequest ignore print log !!!");
    }

    protected abstract <T extends TransmitCallback> void execute(T callback);

    /**
     * 添加请求参数
     *
     * @param paramKey
     * @param paramValue
     * @return
     */
    public R addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.localParams.put(paramKey, paramValue);
        }
        return CastUtils.cast(this);
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    public R addParams(Map<String, String> params) {
        if (params != null) {
            this.localParams.putAll(params);
        }
        return CastUtils.cast(this);
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    public R removeParam(String paramKey) {
        if (paramKey != null) {
            this.localParams.remove(paramKey);
        }
        return CastUtils.cast(this);
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public R params(Map<String, String> params) {
        if (params != null) {
            this.localParams = params;
        }
        return CastUtils.cast(this);
    }

    @Override
    protected void injectLocalParams() {
        super.injectLocalParams();
        // 添加日志拦截器
        if (RxPanda.globalConfig().getLoggingInterceptor() != null) {
            if (!builder.networkInterceptors().contains(RxPanda.globalConfig().getLoggingInterceptor())) {
                builder.addNetworkInterceptor(RxPanda.globalConfig().getLoggingInterceptor());
            }
        }
        RxPanda.getRetrofitBuilder().client(builder.build());
        retrofit = RxPanda.getRetrofitBuilder().build();
        if (mGlobalConfig.getGlobalParams() != null) {
            localParams.putAll(mGlobalConfig.getGlobalParams());
        }
        if (retryDelayMillis <= 0) {
            retryDelayMillis = mGlobalConfig.getRetryDelayMillis();
        }
        mApi = retrofit.create(Api.class);
    }
}
