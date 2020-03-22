package com.pandaq.rxpanda.transformer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.pandaq.rxpanda.entity.EmptyData;
import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.exception.ExceptionType;
import com.pandaq.rxpanda.utils.GsonUtil;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :cast ResponseBody to T
 */
@SuppressWarnings("unchecked")
public class CastFunc<T> implements Function<ResponseBody, T> {

    private Type type;
    private TypeAdapter<T> typeAdapter;

    public CastFunc(Type type) {
        this.type = type;
        this.typeAdapter = (TypeAdapter<T>) GsonUtil.gson().getAdapter(TypeToken.get(type));
    }

    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        String json = responseBody.string();
        if (json == null || json.isEmpty()) {
            if (EmptyData.class.equals(type)) { // 如果接收的是空对象则回传 EmptyData
                return typeAdapter.fromJson(new Gson().toJson(new EmptyData()));
            } else if (String.class.equals(type)) {
                return (T) json;
            } else {
                ApiException exception = new ApiException(-1, "data parse error", json);
                exception.setExceptionType(ExceptionType.JSON_PARSE);
                throw exception;
            }
        } else {
            return GsonUtil.gson().fromJson(json, type);
        }
    }
}
