package com.adl.project.service

import com.adl.project.model.adl.AdlModel
import com.adl.project.model.test.PostModel
import com.adl.project.model.adl.MainResponseModel
import com.glacier.notihttppost.service.UnsafeOkHttpClient
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface HttpService {
    @POST("/shop/api")
    @Headers("accept: application/json",
        "content-type: application/x-www-form-urlencoded","charset:utf-8")
    fun post_users(
        @Body jsonparams: PostModel
    ): Call<MainResponseModel>

    @GET("AB001309")
    fun getMainData(
        @Query("from") from: String, //요구하는 기본인자를 @Query형태로
        @Query("to") to: String
    ): Call<MainResponseModel>

    companion object {
        var okHttpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient

        fun create(BASE_URL: String): HttpService {
//            val gson : Gson = GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HttpService::class.java)
        }
    }
}