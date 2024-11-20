package com.example.rajuk.dataClass

import com.google.gson.annotations.SerializedName

data class ComplainRequest(
    @SerializedName("plot_type") val plotType: String,
    @SerializedName("house_no") val houseNo: String,
    @SerializedName("road") val road: String,
    @SerializedName("city_corporation_id") val cityCorporationId: Int,
    @SerializedName("thana_id") val thanaId: Int,
    @SerializedName("lat") val lat: String?,
    @SerializedName("lon") val lon: String?,
    @SerializedName("details") val details: String?,
    @SerializedName("front_image") val frontImage: String?,
    @SerializedName("back_image") val backImage: String?,
    @SerializedName("right_image") val rightImage: String?,
    @SerializedName("left_image") val leftImage: String?,
    @SerializedName("approval") val approval: String?
)
