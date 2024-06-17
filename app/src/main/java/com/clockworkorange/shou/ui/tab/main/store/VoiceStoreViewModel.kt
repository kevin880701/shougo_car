package com.clockworkorange.shou.ui.tab.main.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.domain.VoiceStore
import com.clockworkorange.shou.ui.base.BaseViewModel

class VoiceStoreViewModel(
    locationObserver: LocationObserver,
    private val repository: SHOURepository,
) : BaseViewModel()  {

    private val _voiceTag =  MutableLiveData<String>()
    val voiceTag : LiveData<String> = _voiceTag
    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    val voiceStores: LiveData<List<VoiceStore>> = _voiceTag.switchMap { tag ->
        liveData {
            _msg.value = ""
            showLoading(true)
            val location = locationObserver.getLastLocation() ?: locationObserver.askCurrentLocation()
            if (location != null) {
                val stores = repository.getNearStores(location.latitude, location.longitude, tag)
                emit(stores)
                if (stores.isEmpty()) _msg.value = "查無資料"
            } else {
                emit(emptyList())
                _msg.value = "無法取得裝置位置，請稍後再試"
            }
            showLoading(false)
        }
    }

    fun setVoiceTag(tag: String) {
        _voiceTag.value = tag
    }
}