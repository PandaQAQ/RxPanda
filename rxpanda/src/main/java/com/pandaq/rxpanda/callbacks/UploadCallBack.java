package com.pandaq.rxpanda.callbacks;

import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.utils.ThreadUtils;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.ResponseBody;

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
public abstract class UploadCallBack extends ApiObserver<ResponseBody> implements TransmitCallback {

    private final boolean autoBackMainThread;

    public UploadCallBack() {
        this.autoBackMainThread = true;
    }

    public UploadCallBack(boolean autoBackMainThread) {
        this.autoBackMainThread = autoBackMainThread;
    }

    @Override
    protected void onSuccess(@NonNull ResponseBody data) {

    }

    @Override
    protected void onError(ApiException e) {
        if (autoBackMainThread) {
            ThreadUtils.getMainHandler().post(() -> onFailed(e));
        } else {
            onFailed(e);
        }
    }

    @Override
    protected void finished(boolean success) {
        if (autoBackMainThread) {
            ThreadUtils.getMainHandler().postDelayed(() -> done(success)
                    , 500);
        } else {
            done(success);
        }
    }

}
