package com.clockworkorange.shou

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.shou.ui.MainViewModel
import com.clockworkorange.shou.ui.login.LoginViewModel
import com.clockworkorange.shou.ui.tab.account.AccountViewModel
import com.clockworkorange.shou.ui.tab.favorite.FavoriteViewModel
import com.clockworkorange.shou.ui.tab.favorite.coupon.FavoriteCouponViewModel
import com.clockworkorange.shou.ui.tab.favorite.placestore.FavoriteStorePlaceViewModel
import com.clockworkorange.shou.ui.tab.main.MainTabViewModel
import com.clockworkorange.shou.ui.tab.main.base.CouponPlaceListViewModel
import com.clockworkorange.shou.ui.tab.main.coupon.CouponDetailViewModel
import com.clockworkorange.shou.ui.tab.main.place.PlaceDetailViewModel
import com.clockworkorange.shou.ui.tab.main.store.VoiceStoreViewModel
import com.clockworkorange.shou.ui.tab.main.topmenu.CityTownViewModel
import com.clockworkorange.shou.ui.tab.main.traveltoadd.SelectTravelToAddViewModel

class AppViewModelFactory(
    private val context: Context,
    private val userRepository: UserRepository,
    private val shouRepository: SHOURepository,
    private val locationObserver: LocationObserver
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            LoginViewModel::class.java -> LoginViewModel(context, userRepository) as T
            MainViewModel::class.java -> MainViewModel(userRepository, shouRepository) as T
            MainTabViewModel::class.java -> MainTabViewModel(userRepository, shouRepository, locationObserver) as T
            AccountViewModel::class.java -> AccountViewModel(userRepository) as T
            FavoriteViewModel::class.java -> FavoriteViewModel(userRepository, shouRepository, locationObserver) as T
            CityTownViewModel::class.java -> CityTownViewModel(shouRepository) as T
            PlaceDetailViewModel::class.java -> PlaceDetailViewModel(shouRepository) as T
            CouponDetailViewModel::class.java -> CouponDetailViewModel(shouRepository) as T
            CouponPlaceListViewModel::class.java -> CouponPlaceListViewModel() as T
            FavoriteCouponViewModel::class.java -> FavoriteCouponViewModel(shouRepository, locationObserver) as T
            FavoriteStorePlaceViewModel::class.java -> FavoriteStorePlaceViewModel(shouRepository, locationObserver) as T
            SelectTravelToAddViewModel::class.java -> SelectTravelToAddViewModel(shouRepository) as T
            VoiceStoreViewModel::class.java -> VoiceStoreViewModel(locationObserver, shouRepository) as T
            else -> throw IllegalArgumentException("not support")
        }
    }
}