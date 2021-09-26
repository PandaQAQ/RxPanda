package com.pandaq.rxpanda.transformer;

import com.pandaq.rxpanda.exception.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description :retry function
 */
public class RetryFunc implements Function<Observable<Throwable>, ObservableSource<?>> {

    private int count = 1;
    private final int maxCount;
    private final long retryDelayMillis;

    public RetryFunc(int maxCount, long retryDelayMillis) {
        this.maxCount = maxCount;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwable) throws Exception {
        return throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
            if (++count <= maxCount && (throwable1 instanceof SocketTimeoutException
                    || throwable1 instanceof ConnectException)) {
                return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
            }
            throw ApiException.handleException(throwable1);
        });
    }
}
