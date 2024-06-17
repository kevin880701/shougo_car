package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName


data class ApiTravel(
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("waypoint_id")
    val waypointId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("date_added")
    val dateAdded: String
)