package com.clockworkorange.repository

import android.location.Location
import com.clockworkorange.repository.domain.*
import com.clockworkorange.repository.model.CityTown
import com.clockworkorange.repository.model.MqttShouData
import com.clockworkorange.repository.model.SearchedItem
import com.clockworkorange.repository.remote.http.ShouServiceWrapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import java.util.*

class SHOURepositoryImpl(private val shouService: ShouServiceWrapper,
                         private val userRepository: UserRepository,
                         private val coroutineScope: CoroutineScope,
                         private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : SHOURepository {

    init {
        coroutineScope.launch {
            userRepository.getAccountState()
                .collect {
                    when(it){
                        is AccountState.LoggedIn -> {
                            refreshFavoriteList()
                        }
                        AccountState.NotLogin -> {
                            favStoreListFlow.emit(mutableListOf())
                            favCouponListFlow.emit(mutableListOf())
                            clearCurrentTravel()
                        }
                    }
                }
        }
    }

    override suspend fun getCityTown(): Map<String, List<CityTown>> {
        return shouService.getCityTown().zips?.groupBy { it.city } ?: emptyMap()
    }

    override suspend fun getStoreByLatLng(location: Location): List<ShouPlace> {
        return shouService.getStoreByLatLng(location).map { it.toShouPlace(shouService) }
    }

    override suspend fun getStoreByAddress(address: String): List<ShouPlace> {
        return shouService.getStoreByAddress(address).map { it.toShouPlace(shouService) }
    }

    override suspend fun getCouponByAddress(address: String): List<ShouCoupon> {
        return shouService.getCouponByAddress(address).map { it.toShouCoupon(shouService) }
    }

    private val favStoreListFlow = MutableStateFlow<List<ShouPlaceDetail>>(emptyList())
    override fun getFavoriteStoreList(): StateFlow<List<ShouPlaceDetail>> {
        coroutineScope.launch(dispatcher) {
            favStoreListFlow.emit(shouService.getFavoriteStoreList().map { it.toShouPlaceDetail(0.0) })
        }
        return favStoreListFlow
    }

    private val favCouponListFlow = MutableStateFlow<List<ShouCouponDetail>>(emptyList())
    override fun getFavoriteCouponList(): StateFlow<List<ShouCouponDetail>> {
        coroutineScope.launch(dispatcher) {
            favCouponListFlow.emit(shouService.getFavoriteCouponList().map { it.toShouCouponDetail() })
        }
        return favCouponListFlow
    }

    override suspend fun refreshFavoriteList() {
        shouService.clearFavoriteCache()
        favStoreListFlow.emit(shouService.getFavoriteStoreList().map { it.toShouPlaceDetail(0.0) })
        favCouponListFlow.emit(shouService.getFavoriteCouponList().map { it.toShouCouponDetail() })
    }

    override fun isInFavoriteList(place: IShouPlace): Boolean {
        return favStoreListFlow.value.count { it.placeId == place.placeId } == 1
    }

    override fun isInFavoriteList(place: IShouCoupon): Boolean {
        return favCouponListFlow.value.count { it.couponId == place.couponId } == 1
    }

    override suspend fun addFavorite(place: IShouPlace): Boolean {
        val res = shouService.addStoreToFavorite(place.placeId)
        refreshFavoriteList()
        return res
    }

    override suspend fun addFavorite(coupon: IShouCoupon): Boolean {
        val res = shouService.addCouponToFavorite(coupon.couponId)
        refreshFavoriteList()
        return res
    }

    override suspend fun deleteFavorite(place: IShouPlace): Boolean {
        val res = shouService.deleteFavoritePlace(place)
        refreshFavoriteList()
        return res
    }

    override suspend fun deleteFavorite(coupon: IShouCoupon): Boolean {
        val res = shouService.deleteFavoriteCoupon(coupon)
        refreshFavoriteList()
        return res
    }

    private val nearStoreFlow = MutableStateFlow<List<ShouPlace>>(emptyList())
    override fun getNearStoreFlow(): StateFlow<List<ShouPlace>> {
        return nearStoreFlow
    }

    private val nearCouponFlow = MutableStateFlow<List<ShouCoupon>>(emptyList())
    override fun getNearCouponFlow(): StateFlow<List<ShouCoupon>> {
        return nearCouponFlow
    }

    override fun onReceiveNearData(data: MqttShouData) {
        val storeList = data.triggers.filter { !it.triggerData.isCoupon() }.map { it.triggerData }
        val couponList = data.triggers.filter { it.triggerData.isCoupon() }.map { it.triggerData }

        coroutineScope.launch(dispatcher) {
            val categories = shouService.getCategoryNameMap()
            val storeData = storeList.map {

                val categoryName = categories.find { category -> category.categoryId == it.categoryId }?.name ?: "unknown"
                ShouPlace(
                    it.storeId,
                    it.storeName,
                    it.lat,
                    it.lng,
                    it.logo,
                    it.categoryId,
                    categoryName,
                    calcDistance(data.lat, data.lng, it.lat, it.lng)
                )
            }

            nearStoreFlow.tryEmit(storeData)

            val couponData = couponList.map {
                val categoryName = categories.find { category -> category.categoryId == it.categoryId }?.name ?: "unknown"

                ShouCoupon(
                    it.couponId!!,
                    it.couponName!!,
                    it.logo,
                    ShouPlace(
                        it.storeId,
                        it.storeName,
                        it.lat,
                        it.lng,
                        "",
                        it.categoryId,
                        categoryName,
                        calcDistance(data.lat, data.lng, it.lat, it.lng)
                    )
                )
            }
            nearCouponFlow.tryEmit(couponData)
        }

    }

    override suspend fun getCouponDetail(coupon: ShouCoupon): ShouCouponDetail {
        return shouService.getCouponDetail(coupon.couponId).toShouCouponDetail(getPlaceDetail(coupon.place))
    }

    override suspend fun getCouponDetail(couponId: Int, place: ShouPlaceDetail): ShouCouponDetail {
        return shouService.getCouponDetail(couponId).toShouCouponDetail(place)
    }

    override suspend fun getPlaceDetail(place: ShouPlace): ShouPlaceDetail {
        return shouService.getStoreDetail(place.placeId).toShouPlaceDetail(place.distance)
    }

    override suspend fun getPlaceDetail(id: Int, lat: Double, lng: Double): ShouPlaceDetail {
        return shouService.getStoreDetail(id, lat, lng).toShouPlaceDetail()
    }

    override suspend fun addFavoriteTravel(name: String, wayPoints: List<TravelWayPoint>): Int {
        val id = shouService.addFavoriteTravel(name, wayPoints)
        refreshFavoriteTravelList()
        return id
    }

    private val currentTravel = MutableStateFlow<MutableList<TravelWayPoint>>(mutableListOf())

    override fun getCurrentTravelFlow(): StateFlow<List<TravelWayPoint>> {
        return currentTravel
    }

    override fun clearCurrentTravel() {
        coroutineScope.launch(dispatcher) {
            currentTravel.emit(mutableListOf())
        }
    }

    override fun addTravel(wayPoint: TravelWayPoint) {
        val newList = mutableListOf<TravelWayPoint>()
        newList.addAll(currentTravel.value)
        //如果需要導航到停車場需要加入兩個點
        //1.店家停車場
        //2.店家
        if (wayPoint.isNavParking){
            newList.add(0, wayPoint.copyWithNavParking(false))
        }
        newList.add(0, wayPoint)
        coroutineScope.launch(dispatcher) {
            currentTravel.emit(newList)
        }
    }

    override suspend fun updateTravel(travel: ShouTravelDetail): Boolean {
        return shouService.updateFavoriteTravel(travel.travelId, travel.name, travel.waypoints)
    }

    override fun deleteTravel(wayPoint: TravelWayPoint) {
        val newList = mutableListOf<TravelWayPoint>()
        newList.addAll(currentTravel.value)
        newList.remove(wayPoint)
        coroutineScope.launch(dispatcher) {
            currentTravel.emit(newList)
        }
    }

    override fun swapTravelOrder(fromPosition: Int, toPosition: Int) {
        val newList = mutableListOf<TravelWayPoint>()
        newList.addAll(currentTravel.value)
        Collections.swap(newList, fromPosition, toPosition)
        coroutineScope.launch(dispatcher) {
            currentTravel.emit(newList)
        }
    }

    override fun isTravelEmpty(): Boolean {
        return currentTravel.value.isEmpty()
    }

    private val favTravelFlow = MutableStateFlow<List<ShouTravel>>(emptyList())
    override fun getFavoriteTravelList(): StateFlow<List<ShouTravel>> {
        refreshFavoriteTravelList()
        return favTravelFlow
    }

    override fun refreshFavoriteTravelList() {
        coroutineScope.launch(dispatcher) {
            favTravelFlow.emit(shouService.getFavoriteTravelList().map { it.toShouTravel() })
        }
    }

    override suspend fun getFavoriteTravelDetail(travelId: Int, lat: Double?, lng: Double?): ShouTravelDetail {
        return shouService.getFavoriteTravelDetail(travelId, lat, lng).toShouTravelDetail(shouService)
    }

    override suspend fun deleteFavoriteTravel(travelId: Int): Boolean {
        val res = shouService.deleteFavoriteTravel(travelId)
        refreshFavoriteTravelList()
        return res
    }

    override fun preLoadData() {
        coroutineScope.launch(dispatcher) {
            shouService.getFavoriteCouponList()
            shouService.getFavoriteStoreList()
        }
    }

    private val newCouponNotificationFlow = MutableStateFlow<NewCouponNotification?>(null)
    override fun onNewCouponReceive(
        title: String,
        msg: String,
        vendorId: Int,
        couponId: Int
    ) {
        val newCouponNotification = NewCouponNotification(title, msg, vendorId, couponId)
        coroutineScope.launch(dispatcher) {
            newCouponNotificationFlow.emit(newCouponNotification)
        }
    }

    override fun getNewCouponNotification(): Flow<NewCouponNotification?> {
        return newCouponNotificationFlow
    }

    override fun newCouponNotificationHandled() {
        coroutineScope.launch(dispatcher) {
            newCouponNotificationFlow.emit(null)
        }
    }

    override suspend fun getNearStores(lat: Double, lng: Double, tag: String): List<VoiceStore> {
        val stores =  shouService.getStoreWithTag(lat, lng, tag)
//        return withContext(dispatcher) { stores.filter { it.distance.toInt() < 2000 }.map { it.toVoiceStore() } }
        return withContext(dispatcher) { stores.map { it.toVoiceStore() } }
    }
}