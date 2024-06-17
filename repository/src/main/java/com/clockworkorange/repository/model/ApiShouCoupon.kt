package com.clockworkorange.repository.model
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.ShouPlace
import com.google.gson.annotations.SerializedName


data class ApiShouCoupon(
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
    @SerializedName("logged")
    val logged: Int,
    @SerializedName("shipping")
    val shipping: Int,
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
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("distance")
    val distance: Double?,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("store_name")
    val storeName: String

)