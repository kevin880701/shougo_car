package com.clockworkorange.shou.ui.tab.favorite

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.repository.domain.AccountState
import com.clockworkorange.repository.domain.IShouCoupon
import com.clockworkorange.repository.domain.IShouPlace
import com.clockworkorange.repository.domain.ShouCouponDetail
import com.clockworkorange.repository.domain.ShouPlaceDetail
import com.clockworkorange.repository.domain.ShouTravel
import com.clockworkorange.repository.domain.ShouTravelDetail
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.ui.base.BaseViewModel
import com.clockworkorange.shou.util.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val userRepository: UserRepository,
    private val shouRepository: SHOURepository,
    private val locationObserver: LocationObserver
) : BaseViewModel() {

    val accountState = userRepository.getAccountState().asLiveData(genericExceptionHandler)

    val favoriteTravelList: LiveData<List<ShouTravel>> = shouRepository.getFavoriteTravelList().asLiveData(genericExceptionHandler)
    val favoriteTravelDetail = MutableLiveData<ShouTravelDetail>()

    val currentTravel = shouRepository.getCurrentTravelFlow()

    val eventShowRequireDeleteTravel = SingleLiveEvent<ShouTravel>()
    val eventShowRequireLogin = SingleLiveEvent<Void>()

    init {
        viewModelScope.launch {
            userRepository.getAccountState()
                .filter { it == AccountState.NotLogin }
                .collect {
                    eventShowRequireLogin.call()
                }
        }
    }

    fun removeFavorite(place: IShouPlace) = viewModelScope.launch(genericExceptionHandler) {
        shouRepository.deleteFavorite(place)
    }

    fun removeFavorite(coupon: IShouCoupon) = viewModelScope.launch(genericExceptionHandler) {
        shouRepository.deleteFavorite(coupon)
    }

    fun addTravel(place: ShouPlaceDetail){
        addTravel(place, false)
    }

    fun addTravel(place: ShouPlaceDetail, requireParking: Boolean) = viewModelScope.launch(genericExceptionHandler) {
        val waypoint = TravelWayPoint.TravelPlace(place, requireParking)
        shouRepository.addTravel(waypoint)
    }

    fun addTravel(coupon: ShouCouponDetail) {
        addTravel(coupon, false)
    }

    fun addTravel(coupon: ShouCouponDetail, requireParking: Boolean) = viewModelScope.launch(genericExceptionHandler) {
        val waypoint = TravelWayPoint.TravelCoupon(coupon, requireParking)
        shouRepository.addTravel(waypoint)
    }

    fun removeFavorite(travel: ShouTravel) = viewModelScope.launch(genericExceptionHandler) {
        shouRepository.deleteFavoriteTravel(travel.travelId)
    }

    fun onRemoveFavoriteTravelClick(item: ShouTravel) {
        eventShowRequireDeleteTravel.value = item
    }

    fun fetchTravelDetail(travel: ShouTravel) = viewModelScope.launch(genericExceptionHandler) {
        //TODO if location not available?
        val currentLocation = locationObserver.getLastLocation()
        if (currentLocation != null){
            val detail = shouRepository.getFavoriteTravelDetail(travel.travelId, currentLocation.latitude, currentLocation.longitude)
            detail?.let { favoriteTravelDetail.value = it }

        }else{
            toast("當前GPS位置不可用")
        }
    }

    fun getLastLocation(): Location? {
        return locationObserver.getLastLocation()
    }


    fun isInCurrentTravel(place: IShouPlace): Boolean{
        return place.placeId in currentTravel.value.map { it.placeId }
    }

    fun isInCurrentTravel(coupon: IShouCoupon): Boolean{
        return coupon.couponId in currentTravel.value.map { it.couponId }
    }

    val eventChangeFavoriteTab = SingleLiveEvent<FavoriteFragment.FavoriteTab>()
    val eventShowStore = SingleLiveEvent<Int>()
    val eventShowCoupon = SingleLiveEvent<Int>()

    fun showStoreInfo(storeId: Int) {
        eventChangeFavoriteTab.value = FavoriteFragment.FavoriteTab.Store
        eventShowStore.value = storeId
    }

    fun checkStoreCoupon(storeId: Int) {
        eventChangeFavoriteTab.value = FavoriteFragment.FavoriteTab.Coupon
        eventShowCoupon.value = storeId
    }

    fun refreshFavorite() = viewModelScope.launch(genericExceptionHandler){
        shouRepository.refreshFavoriteList()
    }

}