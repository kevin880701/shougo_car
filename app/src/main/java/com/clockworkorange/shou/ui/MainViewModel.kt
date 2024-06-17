package com.clockworkorange.shou.ui

import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.repository.model.CityTown
import com.clockworkorange.shou.ui.base.BaseViewModel
import com.clockworkorange.shou.ui.tab.favorite.FavoriteFragment
import com.clockworkorange.shou.ui.tab.main.NavigationAction
import com.clockworkorange.shou.ui.tab.main.VoiceCommand
import com.clockworkorange.shou.util.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class MainViewModel(private val userRepository: UserRepository, private val shouRepository: SHOURepository): BaseViewModel() {

    val eventOpenTab = SingleLiveEvent<MainActivity.APPTabItem>()

    val eventOpenNearStore = SingleLiveEvent<String>()
    val eventSearchAreaCoupon = SingleLiveEvent<CityTown>()
    val eventOpenFavorite = SingleLiveEvent<FavoriteFragment.FavoriteTab>()
    //TODO 新增event開啟搜尋的UI

    val eventNightMode = SingleLiveEvent<Boolean>()

    init {
        if (userRepository.isAppLogin()){
            shouRepository.preLoadData()
        }

        checkNightMode()
    }

    // 處理語音指令
    fun handleNavigationAction(action: NavigationAction) = viewModelScope.launch(genericExceptionHandler){
        //TODO 新增語音搜尋指令
        Timber.d("handleNavigationAction $action")
        when(action.command){

            VoiceCommand.RecommendCoupon -> {//開啟附近coupon列表
                //嗨小莫 我餓了
                //arg=category:餐廳
                //嗨小莫 推薦店家的優惠
                //arg=
                val category = action.arg["category"] ?: ""
                eventOpenTab.value = MainActivity.APPTabItem.Main

                eventOpenNearStore.value = category
            }
            VoiceCommand.RecommendArea -> {//搜尋指定區域的推薦
                //嗨小莫 [台中市烏日區、台北市內湖區] 的推薦
                //arg=area:台中市烏日區
                //arg=area:台北市內湖區
                val area = action.arg["area"] ?: ""
                val cityTowns = shouRepository.getCityTown().values.flatten()
                val targetCityTown: CityTown? = cityTowns.find { area.contentEquals("${it.city}${it.town}") }
                if (targetCityTown == null){
                    toast("找不到$area")
                    return@launch
                }
                eventOpenTab.value = MainActivity.APPTabItem.Main
                eventSearchAreaCoupon.value = targetCityTown!!

            }
            VoiceCommand.FavoriteList -> {//開啟收藏清單
                //嗨小莫 [優惠券、商家、景點] 的收藏清單
                //arg=type:coupon
                //arg=type:store
                //arg=type:place
                when(val type = action.arg["type"]){
                    "coupon" -> {
                        //開啟 優惠券 收藏清單
                        eventOpenTab.value = MainActivity.APPTabItem.Favorite
                        eventOpenFavorite.value = FavoriteFragment.FavoriteTab.Coupon
                    }

                    "store" -> {
                        //開啟 商家 收藏清單
                        eventOpenTab.value = MainActivity.APPTabItem.Favorite
                        eventOpenFavorite.value = FavoriteFragment.FavoriteTab.Store

                    }

                    "place" -> {
                        //開啟 景點 收藏清單
                        eventOpenTab.value = MainActivity.APPTabItem.Favorite
                        eventOpenFavorite.value = FavoriteFragment.FavoriteTab.Place
                    }

                    else -> {
                        toast("不支援此收藏清單($type)")
                    }
                }

            }
            else -> {}
        }

    }

    fun setFcmToken(token: String) = viewModelScope.launch(genericExceptionHandler) {
        val success = userRepository.uploadFCMToken(token)
        if (!success){
            Timber.e("upload fcm token failed.")
        }
    }

    private fun checkNightMode() {
        var isNight = false
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)// Calendar.JANUARY...etc
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)//0-23

        //3-9月
        val summerMonth = listOf(
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
        )

        isNight = if (summerMonth.contains(month)){
            //18-5 夜間
            (hour24 >= 18 || hour24 < 5)
        }else{
            //17-6夜間
            (hour24 >= 17 || hour24 < 6)
        }
        eventNightMode.postValue(isNight)
    }

}
