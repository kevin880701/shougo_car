package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("expires_sec")
    val expiresSec: Int,
    @SerializedName("refresh_token")
    val refreshToken: String
)
