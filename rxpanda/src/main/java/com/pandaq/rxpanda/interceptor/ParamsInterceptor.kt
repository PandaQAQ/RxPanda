package com.pandaq.rxpanda.interceptor;

import io.reactivex.annotations.NonNull;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxinyu on 2019/8/19.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :公共参数拦截器，为 retrofit 请求添加公共参数
 */

public class ParamsInterceptor implements Interceptor {
    private static final String POST = "POST";
    private static final String GET = "GET";

    private Map<String, String> paramsMap = new HashMap<>();

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    /**
     * only support post and get request
     */
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        if (paramsMap.size() <= 0) {
            return chain.proceed(request);
        }
        if (request.method().equals(POST)) {
            if (request.body() instanceof FormBody) {           // 表单方式提交
                FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                for (Object o : paramsMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    newFormBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }
                FormBody oldFormBody = (FormBody) request.body();
                int paramSize = oldFormBody.size();
                for (int i = 0; i < paramSize; i++) {
                    newFormBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i));
                }
                requestBuilder.post(newFormBodyBuilder.build());
            } else if (request.body() instanceof MultipartBody) {           // MultipartBody 方式提交
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                for (Object o : paramsMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    multipartBuilder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
                }

                List<MultipartBody.Part> oldParts = ((MultipartBody) request.body()).parts();
                for (MultipartBody.Part part : oldParts) {
                    multipartBuilder.addPart(part);
                }
                requestBuilder.post(multipartBuilder.build());
            } else if (request.body() != null && request.body().contentLength() == 0L) {
                //无参时提交公共参数
                FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                for (Object o : paramsMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    newFormBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }
                requestBuilder.post(newFormBodyBuilder.build());
            }
        } else if (request.method().equals(GET)) {
            HttpUrl.Builder builder = request.url().newBuilder();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                builder.setQueryParameter(entry.getKey(), entry.getValue());
            }
            requestBuilder.url(builder.build());
        }
        request = requestBuilder.build();
        return chain.proceed(request);
    }
}
