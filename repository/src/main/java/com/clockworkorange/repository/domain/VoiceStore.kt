package com.clockworkorange.repository.domain

data class VoiceStore(
    val id: String,
    val name: String,
    val logoUrl: String,
    val webUrl: String,
    val lat: Double,
    val lng: Double,
    val distance: Int
)
