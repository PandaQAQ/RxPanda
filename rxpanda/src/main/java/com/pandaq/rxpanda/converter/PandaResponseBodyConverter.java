package com.pandaq.rxpanda.converter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.pandaq.rxpanda.HttpCode;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.config.HttpGlobalConfig;
import com.pandaq.rxpanda.entity.EmptyData;
import com.pandaq.rxpanda.entity.IApiData;
import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.exception.ExceptionType;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Converter;

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
    private Type dataType; //定义的解析类型

    @SuppressWarnings("unchecked")
    PandaResponseBodyConverter(Gson gson, Type dataType) {
        this.gson = gson;
        this.typeAdapter = (TypeAdapter<T>) gson.getAdapter(TypeToken.get(dataType));
        this.dataType = dataType;
    }

    @SuppressWarnings("unchecked")
    PandaResponseBodyConverter(Gson gson, Type dataType, Class<? extends IApiData> clazz) {
        this.gson = gson;
        this.typeAdapter = (TypeAdapter<T>) gson.getAdapter(TypeToken.get(dataType));
        this.apiDataClazz = clazz;
        this.dataType = dataType;
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
                throw new ApiException(HttpCode.FRAME_WORK.SHELL_FORMAT_ERROR, response, response);
            } finally {
                value.close();
            }
        } else {
            String data = new Gson().toJson(apiData.getData());
            if (!apiData.isSuccess()) {
                ApiException exception = new ApiException(apiData.getCode(), apiData.getMsg(), data);
                exception.setExceptionType(ExceptionType.API);
                throw exception;
            } else {
                try {
                    Reader reader = new StringReader(data);
                    JsonReader jsonReader = gson.newJsonReader(reader);
                    jsonReader.setLenient(true);
                    T result = typeAdapter.read(jsonReader);
                    if (result == null) {
                        if (dataType.equals(EmptyData.class)) { //如果需要得数据类型为 EmptyData 则不返回数据时也解析为空对象
                            return typeAdapter.fromJson(new Gson().toJson(new EmptyData()));
                        } else {
                            ApiException exception = new ApiException(apiData.getCode(), apiData.getMsg(), data);
                            exception.setExceptionType(ExceptionType.JSON_PARSE);
                            throw exception;
                        }
                    } else {
                        return result;
                    }
                } catch (Exception e) {
                    if (HttpGlobalConfig.getInstance().isDebug()) {
                        e.printStackTrace();
                        Log.w("errorData: ", response);
                    }
                    // 原始数据解析不通返回 EmptyData对象解析
                    throw new ApiException(apiData.getCode(), "接口数据与本地数据结构不匹配", data);
                } finally {
                    value.close();
                }
            }

        }
    }

    /**
     * 获取解析 data 对象的完整类名
     *
     * @return 完整类名
     */
//    private String getClazzName() {
//        String className = null;
//        if (null != dataType) {
//            if (dataType instanceof ParameterizedType) {
//                ParameterizedType pt = (ParameterizedType) dataType;
//                Class clz = (Class) pt.getRawType();
//                className = clz.getName();
//            } else if (dataType instanceof TypeVariable) {
//                TypeVariable tType = (TypeVariable) dataType;
//                className = tType.getGenericDeclaration().toString();
//            } else {
//                Class clz = (Class) dataType;
//                className = clz.getName();
//            }
//        }
//        return className;
//    }
}
