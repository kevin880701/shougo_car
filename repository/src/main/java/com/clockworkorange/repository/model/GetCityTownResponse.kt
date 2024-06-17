package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class GetCityTownResponse(
    @SerializedName("ver")
    val ver: Int,
    @SerializedName("zips")
    val zips: List<CityTown>?
)

data class CityTown(
    @SerializedName("city")
    val city: String,
    @SerializedName("town")
    val town: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("sort")
    val sort: Int
)