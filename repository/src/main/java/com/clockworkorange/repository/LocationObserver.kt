package com.clockworkorange.repository

import android.location.Location
import kotlinx.coroutines.flow.SharedFlow

interface LocationObserver {

    val locationFlow: SharedFlow<Location>

    fun getLastLocation(): Location?

    suspend fun askCurrentLocation(): Location?

    fun start()

    fun stop()

    fun kick()
}