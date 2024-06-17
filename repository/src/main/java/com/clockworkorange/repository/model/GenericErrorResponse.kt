package com.clockworkorange.repository.model

import com.google.gson.annotations.SerializedName

data class GenericErrorResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("errorMsg")
    val errorMsg: String
)