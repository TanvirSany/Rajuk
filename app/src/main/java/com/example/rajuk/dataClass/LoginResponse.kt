package com.example.rajuk.dataClass

import java.util.Date

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User? = null
)


data class User(
    val id: Int? = null,
    val role: String? = null,
    val name: String? = null,
    val empId: String? = null,
    val phone: String? = null,
    val nid: String? = null,
    val image: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null,
)