package com.clockworkorange.shou.ui.tab.main.place

import androidx.lifecycle.*
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.ui.base.BaseViewModel

class PlaceDetailViewModel(private val shouRepository: SHOURepository) : BaseViewModel() {

    private val currentPlace = MutableLiveData<ShouPlace>()
    private val currentTravel = MutableLiveData<List<TravelWayPoint>>()

    val placeDetail = currentPlace.switchMap { liveData(genericExceptionHandler) { emit(shouRepository.getPlaceDetail(it)) } }

    private val currentFavPlaceIdList = shouRepository.getFavoriteStoreList().asLiveData()

    val isFavorite = MediatorLiveData<Boolean>()
    val isInTravel = MediatorLiveData<Boolean>()

    init {

        isFavorite.addSource(currentPlace){
            val placeId = it.placeId
            val favIds = currentFavPlaceIdList.value?.map { it.placeId } ?: return@addSource
            isFavorite.value = placeId in favIds
        }

        isFavorite.addSource(currentFavPlaceIdList){
            val placeId = currentPlace.value?.placeId ?: return@addSource
            val favIds = it.map { it.placeId }
            isFavorite.value = placeId in favIds
        }

        isInTravel.addSource(currentPlace){
            val placeId = it.placeId
            val travelList = currentTravel.value ?: return@addSource

            isInTravel.value = placeId in travelList.map { it.placeId }
        }

        isInTravel.addSource(currentTravel){
            val placeId = currentPlace.value?.placeId ?: return@addSource
            val travelList = it

            isInTravel.value = placeId in travelList.map { it.placeId }
        }
    }

    fun setPlace(place: ShouPlace){
        currentPlace.value = place
    }

    fun setCurrentTravel(travelList: List<TravelWayPoint>) {
        currentTravel.value = travelList
    }


}