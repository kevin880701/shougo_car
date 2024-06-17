package com.clockworkorange.shou.ui.tab.main.traveltoadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.ui.base.BaseViewModel
import com.clockworkorange.shou.util.SingleLiveEvent
import kotlinx.coroutines.launch

class SelectTravelToAddViewModel(private val repository: SHOURepository): BaseViewModel() {

    val travelList: LiveData<List<ShouTravel>> = liveData<List<ShouTravel>>(genericExceptionHandler) {
        showLoading(true)
        emit(repository.getFavoriteTravelList().value)
        showLoading(false)
    }

    val eventExit = SingleLiveEvent<Void>()

    fun addToTravel(waypoint: TravelWayPoint, travel: ShouTravel) = viewModelScope.launch(genericExceptionHandler) {
        val travelDetail = repository.getFavoriteTravelDetail(travel.travelId)
        val editedWayPoints = if (waypoint.isNavParking){
            travelDetail.waypoints + listOf(waypoint,  waypoint.copyWithNavParking(false))
        }else{
            travelDetail.waypoints + listOf(waypoint)
        }
        val editedTravel = travelDetail.copy(waypoints = editedWayPoints)
        val success = repository.updateTravel(editedTravel)
        if (success){
            toast("更新成功")
            eventExit.call()
        }else{
            toast("更新失敗")
        }
    }

    fun addToCurrentTravel(waypoint: TravelWayPoint) = viewModelScope.launch(genericExceptionHandler) {
        repository.addTravel(waypoint)
        eventExit.call()
    }

}