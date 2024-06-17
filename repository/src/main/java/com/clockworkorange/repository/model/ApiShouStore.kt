package com.clockworkorange.repository.model

import com.clockworkorange.repository.domain.ShouPlace
import com.google.gson.annotations.SerializedName

data class ApiShouStore(
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("store_owner")
    val storeOwner: String,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("fax")
    val fax: String,
    @SerializedName("parking")
    val parking: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("facebook")
    val facebook: String,
    @SerializedName("instagram")
    val instagram: String,
    @SerializedName("youtube")
    val youtube: String,
    @SerializedName("twitter")
    val twitter: String,
    @SerializedName("pinterest")
    val pinterest: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("approved")
    val approved: Int,
    @SerializedName("date_added")
    val dateAdded: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("star")
    val star: Double,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("distance")
    val distance: Double?,
    @SerializedName("category_id")
    val categoryId: Int
)