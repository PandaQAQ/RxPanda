package com.pandaq.sample.apis;

import android.util.Log;
import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.observer.ApiObserver;

/**
 * Created by huxinyu on 2019/3/8.
 * Email : panda.h@foxmail.com
 * Description :自定义的处理类
 */
public abstract class AppCallBack<T> extends ApiObserver<T> {
    @Override
    protected void onSuccess(T data) {
        success(data);
    }

    @Override
    protected void onError(ApiException e) {
        handleException(e);
        fail(e.getCode(), e.getMessage());
    }

    @Override
    protected void finished(boolean success) {
        finish(success);
    }

    private void handleException(ApiException e) {
        if (e.getCode() == ExceptionCode.TOKEN_INVALID) {
            Log.e("HttpError","TOKEN 已过期");
        }
    }

    protected abstract void success(T data);

    protected abstract void fail(Long code, String msg);

    protected abstract void finish(boolean success);
}
