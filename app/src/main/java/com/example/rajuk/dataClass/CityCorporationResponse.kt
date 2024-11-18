package com.example.rajuk.dataClass

data class CityCorporationResponse (
    val message: String,
    val cityCorporations: List<CityCorporation>
)

data class CityCorporation(
    val id: Int,
    val name: String,
)

