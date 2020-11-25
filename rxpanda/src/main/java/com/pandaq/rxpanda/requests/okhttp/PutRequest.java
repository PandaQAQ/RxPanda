package com.pandaq.rxpanda.requests.okhttp;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * Created by huxinyu on 2019/3/16.
 * Email : panda.h@foxmail.com
 * <p>
 * Description : request in put method
 */
public class PutRequest extends HttpRequest<PutRequest> {

    public PutRequest(String url) {
        super(url);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        return mApi.put(url, localParams)
                .doOnSubscribe(disposable -> {
                    if (tag != null) {
                        RxPanda.manager().addTag(tag, disposable);
                    }
                })
                .compose(httpTransformer(type));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> void execute(ApiObserver<T> callback) {
        if (tag != null) {
            RxPanda.manager().addTag(tag, callback);
        }
        this.execute(getType(callback))
                .map(o -> (T) o)
                .subscribe(callback);
    }
}
