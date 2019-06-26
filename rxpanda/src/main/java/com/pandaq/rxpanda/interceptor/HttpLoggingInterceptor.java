/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pandaq.rxpanda.interceptor;

import com.pandaq.rxpanda.RxPanda;
import com.pandaq.rxpanda.log.LogEntity;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * {@linkplain OkHttpClient#interceptors() application interceptor} or as a {@linkplain
 * OkHttpClient#networkInterceptors() network interceptor}. <p> The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
public final class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLoggingInterceptor setLevel(Level level) {
        // 非debug模式直接返回 NONE 级别
        if (!RxPanda.globalConfig().isDebug()) return this;

        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;
        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }
        LogEntity entity = new LogEntity();
        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Connection connection = chain.connection();
        String requestStartMessage = "==> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        entity.addLog(requestStartMessage);
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    entity.addLog("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    entity.addLog("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    entity.addLog(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                entity.addLog("Info: " + request.method());
            } else if (bodyHasUnknownEncoding(request.headers())) {
                entity.addLog("Info: " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                if (isPlaintext(buffer)) {
                    if (charset != null) {
                        entity.addLog(buffer.readString(charset));
                    }
                    entity.addLog("Info: " + request.method() + requestBody.contentLength() + "-byte body");
                } else {
                    entity.addLog("Info: " + request.method() + " binary "
                            + requestBody.contentLength() + "-byte body omitted");
                }
            }
        }
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            entity.addLog("Error: " + e.getMessage());
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = 0;
        if (responseBody != null) {
            contentLength = responseBody.contentLength();
        }
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        entity.addLog("==> "
                + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url()
                + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                entity.addLog(headers.name(i) + ": " + headers.value(i));
            }

            if (bodyHasUnknownEncoding(response.headers())) {
                entity.addLog("Info : encoded body omitted");
            } else {
                if (responseBody == null) {
                    return response;
                }
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();
                Long gzippedLength = null;
                if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                    gzippedLength = buffer.size();
                    GzipSource gzippedResponseBody = null;
                    try {
                        gzippedResponseBody = new GzipSource(buffer.clone());
                        buffer = new Buffer();
                        buffer.writeAll(gzippedResponseBody);
                    } catch (Exception e) {
                        entity.addLog("Error: " + e.getMessage());
                    } finally {
                        if (gzippedResponseBody != null) {
                            gzippedResponseBody.close();
                            buffer.close();
                        }
                    }
                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (!isPlaintext(buffer)) {
                    entity.addLog("");
                    entity.addLog("Info: binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0 && charset != null) {
                    entity.addLog(buffer.clone().readString(charset));
                }
                if (gzippedLength != null) {
                    entity.addLog("Info: " + buffer.size() + "-byte, " + gzippedLength + "-gzipped-byte body");
                } else {
                    entity.addLog("Info: " + buffer.size() + "-byte body");
                }
            }
        }
        entity.printLog();
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }
}
