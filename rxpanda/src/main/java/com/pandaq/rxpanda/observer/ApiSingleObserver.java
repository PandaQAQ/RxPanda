package com.pandaq.rxpanda.observer;

import com.pandaq.rxpanda.HttpCode;
import com.pandaq.rxpanda.exception.ApiException;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by huxinyu on 2019/3/8.
 * Email : panda.h@foxmail.com
 * Description : Single 类型的观察回调
 */
public abstract class ApiSingleObserver<T> extends DisposableSingleObserver<T> {
    @Override
    public void onSuccess(T t) {
        success(t);
        finished(true);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, HttpCode.FRAME_WORK.UNKNOWN));
        }
        finished(false);
    }

    /**
     * 成功回调
     *
     * @param data 请求成功后的数据体
     */
    protected abstract void success(T data);

    /**
     * 请求失败回调
     *
     * @param e 失败异常对象
     */
    protected abstract void onError(ApiException e);

    /**
     * 结束回调
     *
     * @param success 是否成功获取到数据
     */
    protected abstract void finished(boolean success);
}
