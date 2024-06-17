package com.clockworkorange.repository.domain


interface IShouTravel{
    val travelId: Int
    val name: String
    val date: String
}

data class ShouTravel(
    override val travelId: Int,
    override val name: String,
    override val date: String
):IShouTravel

data class ShouTravelDetail(
    override val travelId: Int,
    override val name: String,
    override val date: String,
    val waypoints: List<TravelWayPoint>
):IShouTravel

