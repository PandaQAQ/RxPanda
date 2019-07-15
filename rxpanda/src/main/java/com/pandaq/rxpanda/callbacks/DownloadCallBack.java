package com.pandaq.rxpanda.callbacks;

import com.pandaq.rxpanda.exception.ApiException;
import com.pandaq.rxpanda.observer.ApiObserver;
import com.pandaq.rxpanda.utils.ThreadUtils;
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
public abstract class DownloadCallBack extends ApiObserver<ResponseBody> implements TransmitCallback {

    private File targetFile;

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    @Override
    protected void onSuccess(ResponseBody body) {
        if (body == null || targetFile == null) {
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
            e.printStackTrace();
            onFailed(e);
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                onFailed(e);
            }
        }
    }

    @Override
    protected void onError(ApiException e) {
        ThreadUtils.getMainHandler().post(() -> onFailed(e));
    }

    @Override
    protected void finished(boolean success) {
        // Rx回调是下载完就走 complete，需要延迟一下等写入本地文件后再触发结果
        ThreadUtils.getMainHandler().postDelayed(() -> done(success)
                , 500);
    }

}
