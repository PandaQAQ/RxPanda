package com.pandaq.rxpanda.api;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by huxinyu on 2019/2/15.
 * Email : panda.h@foxmail.com
 * Description : http request apis, make requests fit rxJava Observable
 */
public interface Api {

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> maps);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url() String url, @FieldMap Map<String, String> maps);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postForm(@Url() String url, @FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @Body RequestBody requestBody);

    @HEAD()
    Observable<ResponseBody> head(@Url String url, @QueryMap Map<String, String> maps);

    @FormUrlEncoded
    @PUT()
    Observable<ResponseBody> put(@Url() String url, @FieldMap Map<String, String> maps);

    @FormUrlEncoded
    @PATCH()
    Observable<ResponseBody> patch(@Url() String url, @FieldMap Map<String, String> maps);

    @FormUrlEncoded
    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @FieldMap Map<String, String> maps);

    @Streaming
    @GET()
    Observable<ResponseBody> downFile(@Url() String url, @QueryMap Map<String, String> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @Part() List<MultipartBody.Part> parts);
}
