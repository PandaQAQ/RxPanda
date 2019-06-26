package com.pandaq.rxpanda.converter;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.entity.EmptyData;
import com.pandaq.rxpanda.entity.IApiData;
import com.pandaq.rxpanda.exception.ApiException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * Created by huxinyu on 2018/5/31.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :响应实体解析类,对返回数据实体做去壳处理
 */
public class PandaResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private Gson gson;
    private TypeAdapter<T> typeAdapter;
    private Class apiDataClazz;

    PandaResponseBodyConverter(Gson gson, TypeAdapter<T> typeAdapter) {
        this.gson = gson;
        this.typeAdapter = typeAdapter;
    }

    PandaResponseBodyConverter(Gson gson, TypeAdapter<T> typeAdapter, Class<? extends IApiData> clazz) {
        this.gson = gson;
        this.typeAdapter = typeAdapter;
        this.apiDataClazz = clazz;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        String response = value.string();
        if (apiDataClazz == null) {
            apiDataClazz = RxPanda.globalConfig().getApiDataClazz();
        }
        IApiData apiData = gson.fromJson(response, (Type) apiDataClazz);
        /* 如是按约定格式返回数据 apiData 中的 code 是必须的。
         * 因此可以用 code 是否存在来判断数据是否合法
         */
        if (apiData.getCode() == null) {
            try {
                return typeAdapter.fromJson(response);
            } finally {
                value.close();
            }
        } else {
            // 获取解析数据,无 data 数据，默认以空对象替代
            String data = apiData.getData() != null ? new Gson().toJson(apiData.getData()) : "data";
            if (!apiData.isSuccess()) {
                throw new ApiException(apiData.getCode(), apiData.getMsg(), data);
            } else {
                try {
                    Reader reader = new StringReader(data);
                    JsonReader jsonReader = gson.newJsonReader(reader);
                    jsonReader.setLenient(true);
                    return typeAdapter.read(jsonReader);
                } catch (Exception e) {
                    // 原始数据解析不通返回 EmptyData对象解析
                    try {
                        return typeAdapter.fromJson(new Gson().toJson(new EmptyData()));
                    } catch (Exception e1) {
                        throw new ApiException(apiData.getCode(), "illegal data type!!!", data);
                    }
                } finally {
                    value.close();
                }
            }
        }
    }
}
