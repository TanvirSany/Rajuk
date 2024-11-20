package com.example.rajuk.Api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://172.16.0.69:82/rcms/api/"

    val gson = GsonBuilder().serializeNulls().create()


    val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(
            OkHttpClient.Builder().build()

        )
        .build()

}