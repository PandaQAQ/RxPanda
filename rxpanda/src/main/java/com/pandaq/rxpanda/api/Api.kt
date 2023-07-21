package com.pandaq.rxpanda.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Created by huxinyu on 2019/2/15.
 * Email : panda.h@foxmail.com
 * Description : http request apis, make requests fit rxJava Observable
 */
interface Api {
    @GET
    suspend fun get(
        @Url url: String?,
        @QueryMap maps: Map<String, String>?
    ): ResponseBody?

    @FormUrlEncoded
    @POST
    suspend fun post(@Url url: String?, @FieldMap maps: Map<String, String>?): ResponseBody?

    @FormUrlEncoded
    @POST
    suspend fun postForm(@Url url: String?, @FieldMap maps: Map<String, Any>?): ResponseBody?

    @POST
    suspend fun postBody(@Url url: String?, @Body requestBody: RequestBody?): ResponseBody?

    @HEAD
    suspend fun head(@Url url: String?, @QueryMap maps: Map<String, String>?): ResponseBody?

    @FormUrlEncoded
    @PUT
    suspend fun put(@Url url: String?, @FieldMap maps: Map<String, String>?): ResponseBody?

    @FormUrlEncoded
    @PATCH
    suspend fun patch(@Url url: String?, @FieldMap maps: Map<String, String>?): ResponseBody?

    @FormUrlEncoded
    @DELETE
    suspend fun delete(
        @Url url: String?,
        @FieldMap maps: Map<String, String>?
    ): ResponseBody?

    @Streaming
    @GET
    suspend fun downFile(
        @Url url: String?,
        @QueryMap maps: Map<String, String>?
    ): ResponseBody?

    @Multipart
    @POST
    suspend fun uploadFiles(
        @Url url: String?,
        @Part parts: List<MultipartBody.Part?>?
    ): ResponseBody?
}