package com.clockworkorange.shou.ui.tab.main.base

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.shou.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class CouponPlaceListViewModel: BaseViewModel() {

    private val placeData =  MutableLiveData<List<ShouPlace>>()
    private val couponData =  MutableLiveData<List<ShouCoupon>>()

    private val targetCategory = MutableLiveData("")

    val filteredPlace = MediatorLiveData<List<ShouPlace>>() // ShouPlaceListFragment
    val filteredCoupon = MediatorLiveData<List<ShouCoupon>>() // ShouCouponListFragment


    init {
        filteredPlace.addSource(placeData){
            val data = it
            val target = targetCategory.value
            if (target?.isNotEmpty() == true){
                filteredPlace.value = data.filter { it.categoryName.contentEquals(target) }
            }else{
                filteredPlace.value = data
            }
        }

        filteredPlace.addSource(targetCategory){
            val data = placeData.value ?: return@addSource
            val target = it
            if (target?.isNotEmpty() == true){
                filteredPlace.value = data.filter { it.categoryName.contentEquals(target) }
            }else{
                filteredPlace.value = data
            }
        }

        filteredCoupon.addSource(couponData){
            val data = it
            val target = targetCategory.value
            if (target?.isNotEmpty() == true){
                filteredCoupon.value = data.filter { it.place.categoryName.contentEquals(target) }
            }else{
                filteredCoupon.value = data
            }
        }

        filteredCoupon.addSource(targetCategory){
            val data = couponData.value ?: return@addSource
            val target = it
            if (target?.isNotEmpty() == true){
                filteredCoupon.value = data.filter { it.place.categoryName.contentEquals(target) }
            }else{
                filteredCoupon.value = data
            }
        }

    }

    @JvmName("setPlaceData")
    fun setData(data: List<ShouPlace>){
        this.placeData.value = data
    }

    @JvmName("setCouponData")
    fun setData(data: List<ShouCoupon>){
        Timber.d("setData couponData")
        this.couponData.value = data
    }

    fun setNoFilter() {
        targetCategory.value = ""
    }

    fun setFilter(category: String) {
        targetCategory.value = category
    }
}