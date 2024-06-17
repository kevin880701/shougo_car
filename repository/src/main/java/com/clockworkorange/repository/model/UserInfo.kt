package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("email")
    val email: String
)