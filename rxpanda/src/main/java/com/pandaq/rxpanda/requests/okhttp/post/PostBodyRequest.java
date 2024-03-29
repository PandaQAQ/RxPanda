package com.pandaq.rxpanda.requests.okhttp.post;


import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.constants.MediaTypes;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.requests.okhttp.base.HttpRequest;
import com.pandaq.rxpanda.utils.GsonUtil;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by huxinyu on 2019/3/15.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class PostBodyRequest extends HttpRequest<PostBodyRequest> {

    // url中带参数 post
    private StringBuilder stringBuilder = new StringBuilder();

    // body Post
    private RequestBody requestBody;
    // body post 参数类型
    private MediaType mediaType;
    // body post 内容
    private String content;

    public PostBodyRequest(String url) {
        super(url);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        if (stringBuilder.length() != 0) {
            url = url + stringBuilder.toString();
        }
        if (requestBody != null) {
            return mApi.postBody(url, requestBody)
                    .doOnSubscribe(disposable -> {
                        if (tag != null) {
                            RxPanda.manager().addTag(tag, disposable);
                        }
                    })
                    .compose(httpTransformer(type));
        }
        if (content != null && mediaType != null) {
            requestBody = RequestBody.create(mediaType, content);
            return mApi.postBody(url, requestBody)
                    .doOnSubscribe(disposable -> {
                        if (tag != null) {
                            RxPanda.manager().addTag(tag, disposable);
                        }
                    })
                    .compose(httpTransformer(type));
        }
        return mApi.post(url, localParams)
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
    public PostBodyRequest urlParam(String paramKey, String paramValue) {
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


    public PostBodyRequest setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /**
     * 设置 postBody 对象
     *
     * @param body post 对象
     * @return self
     */
    public PostBodyRequest setBody(Object body) {
        this.content = GsonUtil.gson().toJson(body);
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostBodyRequest setString(String string) {
        this.content = string;
        this.mediaType = MediaTypes.TEXT_PLAIN_TYPE;
        return this;
    }

    public PostBodyRequest setHtml(String string) {
        this.content = string;
        this.mediaType = MediaTypes.TEXT_HTML_TYPE;
        return this;
    }

    public PostBodyRequest setString(String string, MediaType mediaType) {
        this.content = string;
        this.mediaType = mediaType;
        return this;
    }

    public PostBodyRequest setXml(String xml) {
        this.content = xml;
        this.mediaType = MediaTypes.APPLICATION_XML_TYPE;
        return this;
    }

    public PostBodyRequest setXmlText(String xml) {
        this.content = xml;
        this.mediaType = MediaTypes.TEXT_XML_TYPE;
        return this;
    }

    public PostBodyRequest setJson(String json) {
        this.content = json;
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostBodyRequest setJson(JSONObject jsonObject) {
        this.content = jsonObject.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostBodyRequest jsonArray(JSONArray jsonArray) {
        this.content = jsonArray.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }
}
