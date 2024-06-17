package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class AddFavoriteTravelBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("waypoints")
    val waypoints: List<ApiWayPoint>
)

data class UpdateFavoriteTravelBody(
    @SerializedName("waypoint_id")
    val waypointId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("waypoints")
    val waypoints: List<ApiWayPoint>
)

data class ApiWayPoint(
    @SerializedName("type")//0:優惠券 1:店家 2:特約停車場 3:自訂地址
    val type: Int,
    @SerializedName("vendor_id")//店家編號
    val vendorId: Int,
    @SerializedName("coupon_id")//優惠券編號
    val couponId: Int,
    @SerializedName("address")//自訂地址
    val address: String,
    @SerializedName("sort")
    val sort: Int
){
    companion object{
        fun createCouponWayPoint(couponId: Int, placeId: Int, sort: Int) = ApiWayPoint(0, placeId, couponId, "", sort)

        fun createStoreWayPoint(placeId: Int, sort: Int) = ApiWayPoint(1, placeId, 0,"", sort)

        fun createParkingWayPoint(placeId: Int, sort: Int) = ApiWayPoint(2, placeId, 0, "", sort)

        fun createCustomAddressWayPoint(address: String, sort: Int) = ApiWayPoint(3, 0, 0, address, sort)
    }
}


