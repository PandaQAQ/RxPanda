package com.pandaq.rxpanda.requests.okhttp.post;


import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

/**
 * Created by huxinyu on 2019/3/15.
 * Email : panda.h@foxmail.com
 * Description :表单方式 post
 */
public class PostFormRequest extends HttpRequest<PostFormRequest> {

    // 表单方式 post
    private Map<String, Object> forms = new LinkedHashMap<>();
    // url中带参数 post
    private StringBuilder stringBuilder = new StringBuilder();


    public PostFormRequest(String url) {
        super(url);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        if (stringBuilder.length() != 0) {
            url = url + stringBuilder.toString();
        }
        if (localParams.size() > 0) {
            forms.putAll(localParams);
        }
        return mApi.postForm(url, forms)
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

    /**
     * post url 中添加参数
     *
     * @param paramKey   key
     * @param paramValue value
     * @return self
     */
    public PostFormRequest urlParams(String paramKey, String paramValue) {
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

    /**
     * @param paramKey   表单数据 key
     * @param paramValue 表单数据 value
     * @return self
     */
    public PostFormRequest formParams(String paramKey, Object paramValue) {
        if (paramKey != null && paramValue != null) {
            forms.put(paramKey, paramValue);
        }
        return this;
    }
}