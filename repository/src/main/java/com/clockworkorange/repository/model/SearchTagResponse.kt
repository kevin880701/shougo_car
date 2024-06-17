package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName


data class SearchTagResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val item: List<SearchedItem>
)

data class SearchedItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("outlineImage")
    val logo: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("store_title")
    val name: String,
    @SerializedName("distance")
    val distance: String,
    @SerializedName("url")
    val webUrl: String
)