package com.pandaq.rxpanda.requests.okhttp;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest;
import io.reactivex.rxjava3.core.Observable;

import java.lang.reflect.Type;

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 * <p>
 * Description : get Request
 */
public class GetRequest extends HttpRequest<GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        return mApi.get(url, localParams)
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
