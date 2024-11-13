package com.example.rajuk.dataClass

import com.google.gson.annotations.SerializedName

//data class SuccessResponse(
//
//    val success : String
//)
//
//
//data class ErrorResponse(
//    val message: String,
//    val errors: Errors
//)
//
//data class Errors(
//    val name: List<String>,
//    val phone: List<String>
//)


data class RegisterResponse(
    val message: String,
    val errors: Errors? = null
)

data class Errors(
    val name: List<String>? = null,
    val phone: List<String>? = null,
    val nid: List<String>? = null,
    val password: List<String>? = null,
    val image: List<String>? = null
)