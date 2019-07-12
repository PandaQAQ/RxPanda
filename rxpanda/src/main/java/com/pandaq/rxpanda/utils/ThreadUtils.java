package com.pandaq.rxpanda.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by huxinyu on 2019/7/12.
 * Email : panda.h@foxmail.com
 * Description :获取全局的 mainHandler
 */
public class ThreadUtils {
    private static Handler mainHandler;

    public static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }
}
