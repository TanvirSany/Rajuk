package com.example.rajuk.dataClass

import com.google.gson.annotations.SerializedName

data class ComplainListResponse(
    @SerializedName("message") val message: String,
    @SerializedName("complains") val complains: List<Complain>
)

data class Complain(
    @SerializedName("id") val id: Int,
    @SerializedName("uid") val uid: String,
    @SerializedName("plot_type") val plotType: String,
    @SerializedName("house_no") val houseNo: String,
    @SerializedName("road") val road: String,
    @SerializedName("city_corporation_id") val cityCorporationId: Int,
    @SerializedName("thana_id") val thanaId: Int,
    @SerializedName("details") val details: String,
    @SerializedName("status") val status: String,
    @SerializedName("city_corporation_name") val cityCorporationName: String,
    @SerializedName("thana_name") val thanaName: String
)
