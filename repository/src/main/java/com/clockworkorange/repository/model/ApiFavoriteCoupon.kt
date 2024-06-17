package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName

data class ApiFavoriteCoupon(
    @SerializedName("coupon_id")
    val couponId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("date_start")
    val dateStart: String,
    @SerializedName("date_end")
    val dateEnd: String,
    @SerializedName("uses_total")
    val usesTotal: Int,
    @SerializedName("uses_customer")
    val usesCustomer: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("date_added")
    val dateAdded: String,
    @SerializedName("use_status")
    val useStatus: Int,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("store_description")
    val storeDescription: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("parking")
    val parkingAddress: String,
    @SerializedName("p_lat")
    val parkingLat: Double,
    @SerializedName("p_lng")
    val parkingLng: Double,
    @SerializedName("zone")
    val zone: String,
    @SerializedName("store_logo")
    val storeLogo: String,
    @SerializedName("star")
    val star: Double,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String
)