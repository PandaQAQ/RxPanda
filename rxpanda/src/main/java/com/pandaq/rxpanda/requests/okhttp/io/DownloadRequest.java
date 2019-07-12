package com.pandaq.rxpanda.requests.okhttp.io;

import android.util.Log;
import com.pandaq.rxpanda.RequestManager;
import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.callbacks.DownloadCallBack;
import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.interceptor.DownloadInterceptor;
import com.pandaq.rxpanda.transformer.RxScheduler;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;


/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
public class DownloadRequest extends IORequest<DownloadRequest> {

    private File targetFile;


    public DownloadRequest(String url) {
        super(url);
    }

    public DownloadRequest target(String dirName, String fileName) {
        File file = new File(dirName);
        if (!file.exists()) {
            boolean result = file.mkdirs();
            if (!result) {
                Log.e("DownloadRequest :", "mkdirs failed !");
            }
        }
        targetFile = new File(dirName + File.separator + fileName);
        if (!targetFile.exists()) {
            try {
                boolean res = targetFile.createNewFile();
                if (!res) {
                    Log.e("DownloadRequest :", "createNewFile failed !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public DownloadRequest target(File file) {
        targetFile = file;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends TransmitCallback> void execute(T t) {
        DownloadCallBack callback;
        if (t instanceof DownloadCallBack) {
            callback = (DownloadCallBack) t;
            callback.setTargetFile(targetFile);
        } else {
            return;
        }
        if (super.tag != null) {
            RequestManager.get().addTag(super.tag, callback);
        }
        mApi.downFile(url, localParams)
                .doOnSubscribe(disposable -> {
                    if (tag != null) {
                        RxPanda.manager().addTag(tag, disposable);
                    }
                })
                .compose(RxScheduler.io())
                .subscribe(callback);
    }

    public void request(DownloadCallBack callback) {
        if (targetFile == null || !targetFile.exists()) {
            Log.d("DownloadRequest", "targeFile not found !!!");
        }
        netInterceptor(new DownloadInterceptor(callback));
        injectLocalParams();
        execute(callback);
    }
}
