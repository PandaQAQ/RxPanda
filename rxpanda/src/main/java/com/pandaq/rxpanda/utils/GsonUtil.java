package com.pandaq.rxpanda.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.pandaq.rxpanda.gsonadapter.ObjectTypeAdapter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :创建 Gson 对象单例，使用反射特殊处理了解析 number 类型都为 double 的问题
 */
public class GsonUtil {

    private static Gson gson;

    public static synchronized Gson gson() {
        if (gson == null) {
            gson = create();
        }
        return gson;
    }

    private static Gson create() {
        Gson gson = new GsonBuilder().create();
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            // 反射替换 Gson 的解析 FACTORY
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(com.google.gson.internal.bind.ObjectTypeAdapter.FACTORY);
                    list.set(i, ObjectTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return gson;
    }
}
