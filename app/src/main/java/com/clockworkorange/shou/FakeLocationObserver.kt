package com.clockworkorange.shou

import android.location.Location
import com.clockworkorange.repository.LocationObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

private const val OneMin = 1 * 60 *1000L
private const val TenSec = 10 * 1000L

@ExperimentalCoroutinesApi
class FakeLocationObserver : LocationObserver {

    override val locationFlow = MutableSharedFlow<Location>()

    init {
        fixPosition()
//        movingPosition()
    }

    private fun fixPosition(){
        GlobalScope.launch(Dispatchers.Main) {
            delay(TenSec)
            locationFlow.emit(createLocation(24.10860822880766, 120.62243498034057  ))
        }
    }

    private fun movingPosition(){
        GlobalScope.launch(Dispatchers.Main) {
            delay(TenSec)
            locationFlow.emit(createLocation(24.10860822880766, 120.62243498034057  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.106431853864, 120.62205178363844    ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.105058791593105, 120.62186487254311 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.104135018603056, 120.62460154060655 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.104019726059317, 120.62734417985504 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.10656727625284, 120.63477611001588  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.11042353619743, 120.64676222624013  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.115344025085783, 120.65549851851183 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.121764804099033, 120.66157760154768 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.12860397505229, 120.668918380558    ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.13521201972352, 120.6657649688159   ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.140214656823336, 120.66554235162494 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.14763492446904, 120.67345500941181  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.15301477202761, 120.66685689436504  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.157358021968466, 120.65921024956263 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.162503448377485, 120.6496901764265  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.167633627885987, 120.65380682890697 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.171096266760074, 120.65974279565629 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.17375773845865, 120.67138017136874  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.172055370103404, 120.68534822060295 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.16492134748897, 120.68490298616466  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.15753302498205, 120.68495864070545  ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.154460778788206, 120.68604389991062 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.144693747578685, 120.68182123257378 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.142433818561003, 120.67957719317711 ))
            delay(TenSec)
            locationFlow.emit(createLocation(24.13763031702336, 120.68488923470872  ))
            delay(TenSec)
        }
    }

    private fun createLocation(lat: Double, lng: Double): Location{
        return Location("fake").apply {
            latitude = lat
            longitude = lng
        }
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun kick() {

    }

    override fun getLastLocation(): Location {
        return createLocation(24.13763031702336, 120.68488923470872)
    }

    override suspend fun askCurrentLocation(): Location? {
        TODO("Not yet implemented")
    }

}