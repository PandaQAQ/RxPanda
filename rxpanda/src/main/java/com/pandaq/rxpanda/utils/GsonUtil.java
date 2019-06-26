package com.pandaq.rxpanda.utils;

import com.google.gson.Gson;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class GsonUtil {

    private static Gson gson;

    public static synchronized Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

}
