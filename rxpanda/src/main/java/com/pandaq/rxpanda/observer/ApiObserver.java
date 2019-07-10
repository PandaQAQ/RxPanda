package com.pandaq.rxpanda.observer;

import com.pandaq.rxpanda.HttpCode;
import com.pandaq.rxpanda.exception.ApiException;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by huxinyu on 2019/3/8.
 * Email : panda.h@foxmail.com
 * Description :网络请求回调订阅
 */
public abstract class ApiObserver<T> extends DisposableObserver<T> {

    private boolean success;

    @Override
    public void onNext(T t) {
        onSuccess(t);
        success = true;
    }

    @Override
    public void onError(Throwable t) {
        if (t instanceof ApiException) {
            onError((ApiException) t);
        } else {
            onError(new ApiException(t, HttpCode.FRAME_WORK.UNKNOWN));
        }
        t.printStackTrace();
        success = false;
        finished(false);
    }

    @Override
    public void onComplete() {
        finished(success);
    }

    /**
     * 成功回调
     *
     * @param data 请求成功后的数据体
     */
    protected abstract void onSuccess(T data);

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
