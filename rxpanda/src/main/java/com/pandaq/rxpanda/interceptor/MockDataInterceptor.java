package com.pandaq.rxpanda.interceptor;

import com.google.gson.JsonObject;
import com.pandaq.rxpanda.constants.MediaTypes;
import com.pandaq.rxpanda.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by huxinyu on 2020/12/27.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class MockDataInterceptor implements Interceptor {

    private String mockJson;

    public MockDataInterceptor(String mockJson) {
        this.mockJson = mockJson;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        chain.proceed(chain.request());
        return new Response.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE, mockJson))
                .message("这是拦截器模拟数据！！！！")
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .build();
    }
}
