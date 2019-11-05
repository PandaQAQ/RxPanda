package com.pandaq.rxpanda.transformer;

import com.pandaq.rxpanda.RxPanda;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huxinyu on 2018/5/27.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :线程转换器，网络请求 subscribeOn io ，observeOn AndroidMain
 */
public class RxScheduler {

    /**
     * 网络请求过程线程转换器，io 线程发射 ui 线程观察，且自带重试机制
     *
     * @param <T> 数据类型
     * @return 指定了在 io 线程执行，UI 线程观察结果的观察对象
     */
    public static <T> ObservableTransformer<T, T> retrySync() {
        return retrySync(RxPanda.globalConfig().getRetryCount());
    }

    /**
     * 网络请求过程线程转换器，io 线程发射 ui 线程观察，且自带重试机制
     *
     * @param count 重试次数
     * @param <T>   数据类型
     * @return 指定了在 io 线程执行，UI 线程观察结果的观察对象
     */
    public static <T> ObservableTransformer<T, T> retrySync(int count) {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryFunc(count,
                        RxPanda.globalConfig().getRetryDelayMillis()));
    }

    /**
     * 普通的异步请求线程转换器，不带网络重试机制
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> sync() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 指定 io 线程
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> io() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * 调用结束后自动解除观察
     *
     * @param <T> 数据流对象
     * @return
     */
    public static <T> ObservableTransformer<T, T> autoDispose() {
        final Disposable[] disposables = new Disposable[1];
        return upstream -> upstream
                .doOnSubscribe(disposable -> disposables[0] = disposable)
                .doOnComplete((Action) () -> {
                    if (disposables[0] != null) {
                        disposables[0].dispose();
                    }
                });
    }
}
