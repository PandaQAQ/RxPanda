package com.pandaq.rxpanda.callbacks;

import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.utils.ThreadUtils;
import okhttp3.ResponseBody;

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
public abstract class UploadCallBack extends ApiObserver<ResponseBody> implements TransmitCallback {

    @Override
    protected void onSuccess(ResponseBody data) {

    }

    @Override
    protected void onError(ApiException e) {
        ThreadUtils.getMainHandler().post(() -> onFailed(e));
    }

    @Override
    protected void finished(boolean success) {
        done(success);
    }

}
