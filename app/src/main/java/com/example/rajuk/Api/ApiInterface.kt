package com.example.rajuk.Api

import ThanaResponse
import com.example.rajuk.dataClass.CityCorporationResponse
import com.example.rajuk.dataClass.EmployeeLoginRequest
import com.example.rajuk.dataClass.RegisterRequest
import com.example.rajuk.dataClass.RegisterResponse
import com.example.rajuk.dataClass.LoginRequest
import com.example.rajuk.dataClass.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest) : Response<RegisterResponse>

    @POST("user-login")
    suspend fun login(@Body loginRequest: LoginRequest) : Response<LoginResponse>

    @POST("employee-login")
    suspend fun employeeLogin(@Body employeeLoginRequest: EmployeeLoginRequest) : Response<LoginResponse>

    @GET("thanas")
    suspend fun thana() : Response<ThanaResponse>

    @GET("city-corporations")
    suspend fun cityCorporation() : Response<CityCorporationResponse>
}