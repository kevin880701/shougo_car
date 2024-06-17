package com.clockworkorange.shou.ui.tab.main.coupon

import androidx.lifecycle.*
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.shou.ui.base.BaseViewModel

class CouponDetailViewModel(private val shouRepository: SHOURepository) : BaseViewModel() {

    private val currentCoupon = MutableLiveData<ShouCoupon>()
    private val currentTravel = MutableLiveData<List<TravelWayPoint>>()

    val couponDetail = currentCoupon.switchMap { liveData(genericExceptionHandler) { emit(shouRepository.getCouponDetail(it)) } }

    private val currentFavCouponIdList = shouRepository.getFavoriteCouponList().asLiveData(genericExceptionHandler)

    val isFavorite = MediatorLiveData<Boolean>()
    val isInTravel = MediatorLiveData<Boolean>()

    init {

        isFavorite.addSource(currentCoupon){
            val couponId = it.couponId
            val favIds = currentFavCouponIdList.value?.map { it.couponId } ?: return@addSource
            isFavorite.value = couponId in favIds
        }

        isFavorite.addSource(currentFavCouponIdList){
            val couponId = currentCoupon.value?.couponId ?: return@addSource
            val favIds = it.map { it.couponId }
            isFavorite.value = couponId in favIds
        }

        isInTravel.addSource(currentCoupon){
            val couponId = it.couponId
            val travelList = currentTravel.value ?: return@addSource

            isInTravel.value = couponId in travelList.map { it.couponId }
        }

        isInTravel.addSource(currentTravel){
            val couponId = currentCoupon.value?.couponId ?: return@addSource
            val travelList = it

            isInTravel.value = couponId in travelList.map { it.couponId }
        }

    }

    fun setCoupon(coupon: ShouCoupon){
        currentCoupon.value = coupon
    }

    fun setCurrentTravel(travelList: List<TravelWayPoint>) {
        currentTravel.value = travelList
    }


}