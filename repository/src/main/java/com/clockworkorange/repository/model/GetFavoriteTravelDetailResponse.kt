package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName


data class GetFavoriteTravelDetailResponse(
    @SerializedName("waypoint_id")
    val waypointId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("date_added")
    val dateAdded: String,
    @SerializedName("waypoints")
    val apiWaypointDetails: List<ApiWaypointDetail>
)

data class ApiWaypointDetail(
    @SerializedName("type")
    val type: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("vendor_name")
    val vendorName: String,
    @SerializedName("vendor_logo")
    val vendorLogo: String,
    @SerializedName("coupon_id")
    val couponId: Int,
    @SerializedName("coupon_name")
    val couponName: String,
    @SerializedName("coupon_logo")
    val couponLogo: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("address")
    val address: String,
    @SerializedName("custom_address")
    val customAddress: String,
    @SerializedName("parking_address")
    val parkingAddress: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("sort")
    val sort: Int
)