package com.pandaq.rxpanda.transformer;

import com.pandaq.rxpanda.utils.GsonUtil;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :cast ResponseBody to T
 */
public class CastFunc<T> implements Function<ResponseBody, T> {

    private Type type;

    public CastFunc(Type type) {
        this.type = type;
    }

    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        String json;
        try {
            json = responseBody.string();
            if (type.equals(String.class)) {
                return (T) json;
            } else {
                return GsonUtil.gson().fromJson(json, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        return null;
    }
}
