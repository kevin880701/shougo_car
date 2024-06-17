package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName

data class ApiSearchCategory(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("clicks")
    val clicks: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String
)