package com.pandaq.rxpanda.interceptor;

import com.pandaq.rxpanda.constants.HeaderKey;
import com.pandaq.rxpanda.utils.ZLibUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huxinyu on 2020/11/23.
 * Email : panda.h@foxmail.com
 * Description :支持 ZLib 压缩的接口数据处理
 */
public class ZLibInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String zipHeader = request.header(HeaderKey.ZIP_HEADER);
        if (zipHeader != null && !zipHeader.isEmpty()) { // 增加一步解压
            Response response = chain.proceed(chain.request());
            if (response.body() != null) {
                ZLibUtils.decompress(response.body().bytes());
            } else {
                return response;
            }
        }
        return chain.proceed(request);
    }
}
