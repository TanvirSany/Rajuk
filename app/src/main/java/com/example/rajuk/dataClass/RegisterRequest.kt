package com.example.rajuk.dataClass

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name : String,
    @SerializedName("phone")
    val phone : String,
    @SerializedName("nid")
    val nid : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("image")
    val image : String
)
