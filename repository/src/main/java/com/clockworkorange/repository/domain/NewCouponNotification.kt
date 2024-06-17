package com.clockworkorange.repository.domain

data class NewCouponNotification(
    val title: String,
    val msg: String,
    val vendorId: Int,
    val couponId: Int
)