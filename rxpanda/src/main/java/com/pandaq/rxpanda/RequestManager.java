package com.pandaq.rxpanda;

import android.util.Log;
import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxinyu on 2019/3/11.
 * Email : panda.h@foxmail.com
 * Description : http requests manage
 */
public class RequestManager {

    private static RequestManager sManager;

    private Map<Object, Disposable> subscribers = new HashMap<>();

    private RequestManager() {

    }

    static synchronized RequestManager get() {
        if (sManager == null) {
            sManager = new RequestManager();
        }
        return sManager;
    }

    public void addTag(@NonNull Object tag, @NonNull Disposable disposable) {
        if (subscribers.containsKey(tag)) {
            Log.w("RxPanda","the tag: " + tag + " has been used by another request !!!");
        }
        subscribers.put(tag, disposable);
    }

    public void addTags(@NonNull Map<Object, Disposable> tags) {
        for (Object key : tags.keySet()) {
            if (subscribers.containsKey(key)) {
                Log.w("RxPanda","the tag: " + key + " has been used by another request !!!");
            }
        }
        subscribers.putAll(tags);
    }

    /**
     * 移除指定 tag
     *
     * @param tag 被移除的 tag
     */
    public void removeTag(@NonNull Object tag) {
        subscribers.remove(tag);
    }

    /**
     * 移除指定 tag
     *
     * @param tags 被移除的 tag
     */
    public void removeTags(@NonNull Map<Object, Disposable> tags) {
        subscribers.remove(tags);
    }

    /**
     * 移除所有管理请求
     */
    public void removeAll() {
        subscribers.clear();
    }

    /**
     * 取消指定 tag
     *
     * @param tag 指定tag
     */
    public void cancelTag(@NonNull Object tag) {
        Disposable disposable = subscribers.get(tag);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 根据 tag 取消网络回调
     *
     * @param tags 被取消的请求的 tag
     */
    public void cancelTags(@NonNull Object... tags) {
        for (Object tag : tags) {
            Disposable disposable = subscribers.get(tag);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    /**
     * 取消所有已经加入管理的网络请求监听
     */
    public void cancelAll() {
        for (Map.Entry<Object, Disposable> entry : subscribers.entrySet()) {
            Disposable disposable = entry.getValue();
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }
}
