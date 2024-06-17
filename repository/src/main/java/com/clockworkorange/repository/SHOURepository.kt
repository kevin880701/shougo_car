package com.clockworkorange.repository

import android.location.Location
import com.clockworkorange.repository.domain.*
import com.clockworkorange.repository.model.CityTown
import com.clockworkorange.repository.model.MqttShouData
import com.clockworkorange.repository.model.SearchedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SHOURepository {

    suspend fun getCityTown(): Map<String, List<CityTown>>

    suspend fun getStoreByLatLng(location: Location): List<ShouPlace>

    suspend fun getStoreByAddress(address: String): List<ShouPlace>

    suspend fun getCouponByAddress(address: String): List<ShouCoupon>

    fun getFavoriteStoreList(): StateFlow<List<ShouPlaceDetail>>

    fun getFavoriteCouponList(): StateFlow<List<ShouCouponDetail>>

    suspend fun refreshFavoriteList()

    fun isInFavoriteList(place: IShouPlace): Boolean

    fun isInFavoriteList(place: IShouCoupon): Boolean

    suspend fun addFavorite(place: IShouPlace): Boolean

    suspend fun addFavorite(coupon: IShouCoupon): Boolean

    fun getNearStoreFlow(): StateFlow<List<ShouPlace>>

    fun getNearCouponFlow(): StateFlow<List<ShouCoupon>>

    fun onReceiveNearData(data: MqttShouData)

    suspend fun getCouponDetail(coupon: ShouCoupon): ShouCouponDetail

    suspend fun getCouponDetail(couponId: Int, place: ShouPlaceDetail): ShouCouponDetail

    suspend fun getPlaceDetail(place: ShouPlace): ShouPlaceDetail

    suspend fun getPlaceDetail(id: Int, lat: Double, lng: Double): ShouPlaceDetail

    suspend fun addFavoriteTravel(name: String, wayPoints: List<TravelWayPoint>): Int

    suspend fun deleteFavorite(place: IShouPlace): Boolean

    suspend fun deleteFavorite(coupon: IShouCoupon): Boolean

    fun getCurrentTravelFlow(): StateFlow<List<TravelWayPoint>>

    fun addTravel(wayPoint: TravelWayPoint)

    suspend fun updateTravel(travel: ShouTravelDetail): Boolean

    fun deleteTravel(wayPoint: TravelWayPoint)

    fun clearCurrentTravel()

    fun swapTravelOrder(fromPosition: Int, toPosition: Int)

    fun isTravelEmpty(): Boolean

    fun getFavoriteTravelList(): StateFlow<List<ShouTravel>>

    fun refreshFavoriteTravelList()

    suspend fun getFavoriteTravelDetail(travelId: Int, lat: Double? = null, lng: Double? = null): ShouTravelDetail

    suspend fun deleteFavoriteTravel(travelId: Int): Boolean

    fun preLoadData()

    fun onNewCouponReceive(title: String, msg: String, vendorId: Int, couponId: Int)

    fun getNewCouponNotification(): Flow<NewCouponNotification?>

    fun newCouponNotificationHandled()

    suspend fun getNearStores(lat: Double, lng: Double, tag: String): List<VoiceStore>

}