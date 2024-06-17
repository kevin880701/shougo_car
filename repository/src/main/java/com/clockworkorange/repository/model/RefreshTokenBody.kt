package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenBody(
    @SerializedName("refresh_token")
    var refreshToken: String
)