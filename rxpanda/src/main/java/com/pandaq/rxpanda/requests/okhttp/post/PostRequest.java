package com.pandaq.rxpanda.requests.okhttp.post;


import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest;
import io.reactivex.Observable;

import java.lang.reflect.Type;

/**
 * Created by huxinyu on 2019/1/11.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :a sample request
 */
public class PostRequest extends HttpRequest<PostRequest> {

    // url中带参数 post
    private StringBuilder stringBuilder = new StringBuilder();


    public PostRequest(String url) {
        super(url);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        if (stringBuilder.length() != 0) {
            url = url + stringBuilder.toString();
        }
        return mApi.post(url, globalParams)
                .doOnSubscribe(disposable -> {
                    if (tag != null) {
                        RxPanda.manager().addTag(tag, disposable);
                    }
                })
                .compose(httpTransformer(type));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(ApiObserver callback) {
        if (tag != null) {
            RxPanda.manager().addTag(tag, callback);
        }
        //获取到callback 中的泛型类型
        this.execute(getType(callback)).subscribe(callback);
    }

    /**
     * post url 中添加参数
     *
     * @param paramKey key
     * @param paramValue value
     * @return self
     */
    public PostRequest urlParams(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(paramKey).append("=").append(paramValue);
        }
        return this;
    }
}
