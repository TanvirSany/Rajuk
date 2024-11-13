package com.example.rajuk.Api

import com.example.rajuk.dataClass.RegisterRequest
import com.example.rajuk.dataClass.RegisterResponse
import retrofit2.Response
import retrofit2.http.POST

interface ApiInterface {

    @POST("register")
    suspend fun register(registerRequest: RegisterRequest) : Response<RegisterResponse>
}