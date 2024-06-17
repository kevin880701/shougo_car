package com.clockworkorange.shou.ui.tab.favorite.placestore

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.ShouPlaceDetail
import com.clockworkorange.shou.ui.base.BaseViewModel

class FavoriteStorePlaceViewModel(private val shouRepository: SHOURepository, private val locationObserver: LocationObserver): BaseViewModel() {

    private var predicate: (ShouPlaceDetail) -> Boolean = { true }

    private val favoritePlaceList = shouRepository.getFavoriteStoreList().asLiveData(genericExceptionHandler).map {
        it.forEach { it.calDistanceFromLocation(locationObserver.getLastLocation()) }
        it
    }
    private val filteredCategoryPlaceList = favoritePlaceList.map { it.filter(predicate) }
    val displayPlaceList = MediatorLiveData<List<ShouPlaceDetail>>()

    private val selectedCity = MutableLiveData("")
    private val selectedCategory = MutableLiveData("")

    val cityList = filteredCategoryPlaceList.map { it.map { it.city }.toSet().toList() }
    val categoryList = filteredCategoryPlaceList.map { it.map { it.categoryName }.toSet().toList() }

    init {
        displayPlaceList.addSource(filteredCategoryPlaceList){
            val data = it
            val targetCity = selectedCity.value ?: return@addSource
            val targetCategory = selectedCategory.value ?: return@addSource
            filterData(data, targetCity, targetCategory)
        }

        displayPlaceList.addSource(selectedCity){
            val data = filteredCategoryPlaceList.value ?: return@addSource
            val targetCity = it
            val targetCategory = selectedCategory.value ?: return@addSource
            filterData(data, targetCity, targetCategory)
        }

        displayPlaceList.addSource(selectedCategory){
            val data = filteredCategoryPlaceList.value ?: return@addSource
            val targetCity = selectedCity.value ?: return@addSource
            val targetCategory = it
            filterData(data, targetCity, targetCategory)
        }

    }

    private fun filterData(
        data: List<ShouPlaceDetail>,
        targetCity: String,
        targetCategory: String
    ) {
        displayPlaceList.value = data.filter {
            if (targetCity.isEmpty()) {
                return@filter true
            }
            it.city.contentEquals(targetCity)
        }
            .filter {
                if (targetCategory.isEmpty()) {
                    return@filter true
                }
                it.categoryName.contentEquals(targetCategory)
            }
    }

    fun setPreFilterCategory(predicate: (ShouPlaceDetail) -> Boolean){
        this.predicate = predicate
    }

    fun setCity(name: String){
        selectedCity.value = name
    }

    fun setCategory(name: String){
        selectedCategory.value = name
    }

    fun isFavListEmpty(): Boolean = favoritePlaceList.value?.isEmpty() == true

}