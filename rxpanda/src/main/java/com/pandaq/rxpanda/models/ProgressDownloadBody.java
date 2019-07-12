package com.pandaq.rxpanda.models;


import androidx.annotation.NonNull;
import com.pandaq.rxpanda.callbacks.TransmitCallback;
import com.pandaq.rxpanda.utils.ThreadUtils;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import java.io.IOException;

/**
 * Created by huxinyu on 2018/6/8.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :待写入进度的响应体
 */
public class ProgressDownloadBody extends ResponseBody {
    //实际的待包装响应体
    private final ResponseBody responseBody;
    //进度回调接口
    private final TransmitCallback mCallback;
    //包装完成的BufferedSource
    private BufferedSource bufferedSource;

    /**
     * 构造函数，赋值
     *
     * @param responseBody 待包装的响应体
     * @param mCallback    回调接口
     */
    public ProgressDownloadBody(ResponseBody responseBody, TransmitCallback mCallback) {
        this.responseBody = responseBody;
        this.mCallback = mCallback;
    }


    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     */
    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     */
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            //包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                ThreadUtils.getMainHandler().post(() -> sendStatus(totalBytesRead, bytesRead));
                return bytesRead;
            }
        };
    }

    private void sendStatus(long totalBytesRead, long bytesRead) {
        if (mCallback != null) {
            mCallback.inProgress((int) ((totalBytesRead * 1f / contentLength()) * 100));
        }
    }
}
