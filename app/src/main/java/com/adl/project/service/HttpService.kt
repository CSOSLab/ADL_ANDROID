package com.adl.project.service

import PostModel
import ResponseModel
import com.glacier.notihttppost.service.UnsafeOkHttpClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface HttpService {
    @POST("/shop/api")
    @Headers("accept: application/json",
        "content-type: application/x-www-form-urlencoded","charset:utf-8")
    fun post_users(
        @Body jsonparams: PostModel
    ): Call<ResponseModel>

    companion object {
        var okHttpClient: OkHttpClient = UnsafeOkHttpClient.unsafeOkHttpClient

        fun create(BASE_URL: String): HttpService {
            val gson : Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(HttpService::class.java)
        }
    }
}