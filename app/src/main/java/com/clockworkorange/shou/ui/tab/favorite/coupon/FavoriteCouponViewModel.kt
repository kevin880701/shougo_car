package com.clockworkorange.shou.ui.tab.favorite.coupon

import androidx.lifecycle.*
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouCouponDetail
import com.clockworkorange.shou.ui.base.BaseViewModel

class FavoriteCouponViewModel(private val shouRepository: SHOURepository, private val locationObserver: LocationObserver): BaseViewModel() {

    private val couponList = shouRepository.getFavoriteCouponList().asLiveData(genericExceptionHandler).map {
        it.forEach { it.place.calDistanceFromLocation(locationObserver.getLastLocation()) }
        it
    }

    private val selectedCity = MutableLiveData("")
    private val selectedCategory = MutableLiveData("")

    val cityList = couponList.map { it.map { it.place.city }.toSet().toList() }

    val categoryList = couponList.map { it.map { it.place.categoryName }.toSet().toList() }

    val filteredCoupon = MediatorLiveData<List<ShouCouponDetail>>()

    init {
        filteredCoupon.addSource(couponList){
            val data = it ?: return@addSource
            val targetCity = selectedCity.value ?: return@addSource
            val targetCategory = selectedCategory.value ?: return@addSource
            filterCoupon(data, targetCity, targetCategory)
        }

        filteredCoupon.addSource(selectedCity){
            val data = couponList.value ?: return@addSource
            val targetCity = it
            val targetCategory = selectedCategory.value ?: return@addSource
            filterCoupon(data, targetCity, targetCategory)
        }

        filteredCoupon.addSource(selectedCategory){
            val data = couponList.value ?: return@addSource
            val targetCity = selectedCity.value ?: return@addSource
            val targetCategory = it
            filterCoupon(data, targetCity, targetCategory)
        }

    }

    private fun filterCoupon(data: List<ShouCouponDetail>, targetCity: String, targetCategory: String) {
        filteredCoupon.value =
            data.filter {
                    if (targetCity.isEmpty()) return@filter true
                    it.place.city.contentEquals(targetCity)
                }
                .filter {
                    if (targetCategory.isEmpty()) return@filter true
                    it.place.categoryName.contentEquals(targetCategory)
                }
                .sortedBy { it.place.placeId }
    }


    fun setFilterCity(name: String){
        selectedCity.value = name
    }

    fun setFilterCategory(name: String){
        selectedCategory.value = name
    }

    fun isFavListEmpty(): Boolean = couponList.value?.isEmpty() == true
}