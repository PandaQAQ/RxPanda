package com.pandaq.rxpanda.requests.okhttp.base;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.pandaq.rxpanda.api.Api;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.Request;
import com.pandaq.rxpanda.transformer.CastFunc;
import com.pandaq.rxpanda.transformer.RxScheduler;
import com.pandaq.rxpanda.utils.CastUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.ResponseBody;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 * <p>
 * Description : baseRequest for use okHttp lib, also can return an observable response
 */
public abstract class HttpRequest<R extends HttpRequest> extends Request<R> {

    // http api,兼容 rxJava 观察者模式，需要返回观察对象时，将请求转换成 Retrofit 去请求
    protected Api mApi;
    protected String url = "";
    // request tag
    protected Object tag;
    protected Map<String, String> globalParams = new LinkedHashMap<>();//请求参数
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

    public HttpRequest(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.url = url;
        }
    }

    public <T> Observable<T> request(Type type) {
        injectLocalParams();
        return execute(type);
    }

    public void request(ApiObserver callback) {
        injectLocalParams();
        execute(callback);
    }

    protected abstract <T> Observable<T> execute(Type type);

    protected abstract void execute(ApiObserver callback);

    @Override
    protected void injectLocalParams() {
        super.injectLocalParams();
        if (mGlobalConfig.getGlobalParams() != null) {
            globalParams.putAll(mGlobalConfig.getGlobalParams());
        }
        if (retryDelayMillis <= 0) {
            retryDelayMillis = mGlobalConfig.getRetryDelayMillis();
        }
        mApi = retrofit.create(Api.class);
    }

    protected <T> ObservableTransformer<ResponseBody, T> httpTransformer(final Type type) {
        return apiResultObservable -> apiResultObservable
                .compose(RxScheduler.retrySync())
                .map(new CastFunc<T>(type));
    }
}
