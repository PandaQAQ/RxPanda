package com.pandaq.rxpanda.callbacks;

import com.pandaq.rxpanda.HttpCode;
import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.utils.ThreadUtils;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by huxinyu on 2019/7/11.
 * Email : panda.h@foxmail.com
 * Description :
 */
public abstract class DownloadCallBack extends DisposableObserver<ResponseBody> implements TransmitCallback {

    private File targetFile;
    private boolean success = true;

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    @Override
    public void onNext(ResponseBody body) {
        if (body == null || targetFile == null) {
            success = false;
            onError(new IOException());
            return;
        }
        InputStream is = body.byteStream();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            int len;
            byte[] buffer = new byte[2048];
            while (-1 != (len = is.read(buffer))) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            success = false;
            onError(e);
            return;
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                is.close();
            } catch (Exception e) {
                success = false;
                onError(e);
            }
        }
        // 成功
        ThreadUtils.getMainHandler().post(() -> done(success));
    }

    @Override
    public void onError(Throwable e) {
        ThreadUtils.getMainHandler().post(() -> {
            if (e instanceof ApiException) {
                onFailed((ApiException) e);
            } else {
                onFailed(new ApiException(e, HttpCode.FRAME_WORK.UNKNOWN));
            }
            done(false);
        });
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }

}
