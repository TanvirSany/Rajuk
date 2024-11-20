package com.example.rajuk.dataClass

import com.google.gson.annotations.SerializedName

data class ComplainResponse(
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: ComplainErrors? = null
)

data class ComplainErrors(
    @SerializedName("plot_type") val plotType: List<String>? = null,
    @SerializedName("house_no") val houseNo: List<String>? = null,
    @SerializedName("road") val road: List<String>? = null,
    @SerializedName("city_corporation_id") val cityCorporationId: List<String>? = null,
    @SerializedName("thana_id") val thanaId: List<String>? = null,
    @SerializedName("details") val details: List<String>? = null
)