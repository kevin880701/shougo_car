package com.clockworkorange.shou.ui.tab.main.topmenu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.model.CityTown
import com.clockworkorange.shou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class CityTownViewModel(val repository: SHOURepository): BaseViewModel() {

    private val data:  MutableMap<String, List<CityTown>> = mutableMapOf()
    private val selectedCity = MutableLiveData<String>()
    private val selectedTown = MutableLiveData<String>()

    val cityList =  MutableLiveData<List<String>>()
    val townList =  selectedCity.map { data[it]?.map { it.town } ?: emptyList() }

    init {
        viewModelScope.launch(genericExceptionHandler) {
            data.clear()
            data.putAll(repository.getCityTown())
            cityList.value = data.keys.toList()
        }
    }

    fun refresh(){
        cityList.value = data.keys.toList()
    }

    fun selectCity(city: String){
        selectedCity.value = city
    }

    fun selectTown(town: String){
        selectedTown.value = town
    }

    fun isCitySelected(): Boolean {
        return selectedCity.value != null
    }

    fun isTownSelected(): Boolean{
        return selectedTown.value != null
    }

    fun getSelectedCityTown(): CityTown{
        return data[selectedCity.value]!!.find { it.town.contentEquals(selectedTown.value!!) }!!
    }

}