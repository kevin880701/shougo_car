package com.clockworkorange.shou

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.shou.util.hasLocationPermission
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import kotlin.coroutines.resume


@ExperimentalCoroutinesApi
class LocationObserverImpl(private val context: Context,
                           private val coroutineScope: CoroutineScope,
                           private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : LocationObserver {

    private var locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 1500
        fastestInterval = 1500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var lastLocation: Location? = null

    private var locationUpdateCallback: LocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            Timber.d("onLocationResult ${locationResult.lastLocation}")
            lastLocation = locationResult.lastLocation
            lastLocation?.let { updateLocation(it) }
        }
    }

    override val locationFlow = MutableSharedFlow<Location>(replay = 1)

    init {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful){
                    val location = it.result ?: return@addOnCompleteListener
                    coroutineScope.launch(dispatcher) {
                        Timber.d("last location $location")
                        updateLocation(location)
                        lastLocation = location
                    }
                }
            }
        }
    }

    private fun updateLocation(location: Location) {
        Timber.d("updateLocation")
        coroutineScope.launch(dispatcher) {
            Timber.d("locationFlow emit")
            locationFlow.emit(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun start() {
        if (!hasLocationPermission(context)) {
            Timber.d("尚未取得GPS權限")
        } else {
            Timber.d("開始更新GPS位置")
            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdateCallback, Looper.myLooper())
        }
    }

    override fun stop(){
        fusedLocationClient.removeLocationUpdates(locationUpdateCallback)
    }

    override fun kick() {
        coroutineScope.launch(dispatcher){
            lastLocation?.let { locationFlow.emit(it) }
        }
    }

    override fun getLastLocation(): Location? {
        return lastLocation
    }

    /**
     * get more recent location by "getCurrentLocation" in suspend form
     */
    @SuppressLint("MissingPermission")
    override suspend fun askCurrentLocation(): Location? = suspendCancellableCoroutine<Location?> { continuation ->
        if (!hasLocationPermission(context)) {
            continuation.resume(null)
            Timber.d("尚未取得GPS權限")
        } else {
            val locationCanceler = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, locationCanceler.token)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // 如果 GPS 模組拔除，一樣會是 success, 但 result = null
                        Timber.d("getCurrentLocation success: ${it.result}")
                        continuation.resume(it.result)
                    } else {
                        Timber.d("getCurrentLocation fail")
                        continuation.resume(null)
                    }
                }
            continuation.invokeOnCancellation { locationCanceler.cancel() }
        }
    }
}