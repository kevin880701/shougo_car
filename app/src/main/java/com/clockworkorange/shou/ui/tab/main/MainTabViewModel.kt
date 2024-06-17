package com.clockworkorange.shou.ui.tab.main

import android.location.Location
import androidx.lifecycle.*
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.repository.domain.*
import com.clockworkorange.repository.model.CityTown
import com.clockworkorange.repository.toShouCoupon
import com.clockworkorange.shou.ext.combine
import com.clockworkorange.shou.ui.base.BaseViewModel
import com.clockworkorange.shou.util.SingleLiveEvent
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class MainTabViewModel(
    private val userRepository: UserRepository,
    private val repository: SHOURepository,
    private val locationObserver: LocationObserver
) : BaseViewModel() {

    enum class DataSourceOption {
        Area, Location, Dest
    }

    sealed class DataSource {
        class Area(val cityTown: CityTown) : DataSource()
        object Location : DataSource()
        class Dest(val cityTown: CityTown, val address: String) : DataSource()
    }

    val currentDataSource = MutableLiveData<DataSource>(DataSource.Location)

    val displayCoupon: LiveData<List<ShouCoupon>> = currentDataSource.switchMap { dataSource ->
        Timber.d("source is ${dataSource::class.java.simpleName}")
        return@switchMap when (dataSource) {
            is DataSource.Area -> liveData { emit(repository.getCouponByAddress("${dataSource.cityTown.city}${dataSource.cityTown.town}")) }
            is DataSource.Dest -> liveData { emit(repository.getCouponByAddress("${dataSource.cityTown.city}${dataSource.cityTown.town}${dataSource.address}")) }
            DataSource.Location -> repository.getNearCouponFlow()
                .asLiveData(genericExceptionHandler)
        }
    }

    val displayPlace: LiveData<List<ShouPlace>> = currentDataSource.switchMap { dataSource ->
        return@switchMap when (dataSource) {
            is DataSource.Area -> liveData { emit(repository.getStoreByAddress("${dataSource.cityTown.city}${dataSource.cityTown.town}")) }
            is DataSource.Dest -> liveData { emit(repository.getStoreByAddress("${dataSource.cityTown.city}${dataSource.cityTown.town}${dataSource.address}")) }
            DataSource.Location -> repository.getNearStoreFlow().asLiveData(genericExceptionHandler)
        }
    }

    private val favCouponIds = repository.getFavoriteCouponList().map { it.map { it.couponId } }.asLiveData(genericExceptionHandler)
    private val favPlaceIds = repository.getFavoriteStoreList().map { it.map { it.placeId } }.asLiveData(genericExceptionHandler)

    val shortcutCoupon1LikeState: LiveData<Boolean> = favCouponIds.combine(displayCoupon){ favIds, coupon ->
        val id = coupon.getOrNull(0)?.couponId ?: return@combine false
        return@combine favIds.contains(id)
    }

    val shortcutCoupon2LikeState: LiveData<Boolean> = favCouponIds.combine(displayCoupon){ favIds, coupon ->
        val id = coupon.getOrNull(1)?.couponId ?: return@combine false
        return@combine favIds.contains(id)
    }

    val shortcutCoupon3LikeState: LiveData<Boolean> = favCouponIds.combine(displayCoupon){ favIds, coupon ->
        val id = coupon.getOrNull(2)?.couponId ?: return@combine false
        return@combine favIds.contains(id)
    }

    val shortcutPlace1LikeState: LiveData<Boolean> = favPlaceIds.combine(displayPlace){ favIds, place ->
        val id = place.getOrNull(0)?.placeId ?: return@combine false
        return@combine favIds.contains(id)
    }

    val shortcutPlace2LikeState: LiveData<Boolean> = favPlaceIds.combine(displayPlace){ favIds, place ->
        val id = place.getOrNull(1)?.placeId ?: return@combine false
        return@combine favIds.contains(id)
    }

    val shortcutPlace3LikeState: LiveData<Boolean> = favPlaceIds.combine(displayPlace){ favIds, place ->
        val id = place.getOrNull(2)?.placeId ?: return@combine false
        return@combine favIds.contains(id)
    }


    val currentLocation: LiveData<Location> =
        locationObserver.locationFlow.asLiveData(genericExceptionHandler)
    val displayCouponPlaceBounds = MediatorLiveData<List<LatLng>>()

    val currentTravel = repository.getCurrentTravelFlow().asLiveData(genericExceptionHandler)
    val currentTravelCount = currentTravel.map { it.size }

    val currentFavPlaceIdList =
        repository.getFavoriteStoreList().asLiveData(genericExceptionHandler)
            .map { it.map { it.placeId } }
    val currentFavCouponIdList =
        repository.getFavoriteCouponList().asLiveData(genericExceptionHandler)
            .map { it.map { it.couponId } }

    val notificationNewCoupon =
        repository.getNewCouponNotification().asLiveData(genericExceptionHandler)

    val eventShowSelectDataSource = MutableLiveData<DataSourceOption>()

    val eventShowCouponList = SingleLiveEvent<Void>()
    val eventShowCouponDetail = SingleLiveEvent<ShouCoupon>()

    val eventShowPlaceList = SingleLiveEvent<Void>()
    val eventShowPlaceDetail = SingleLiveEvent<ShouPlace>()

    val eventShowCouponsByPlaceId = SingleLiveEvent<List<ShouCoupon>>()

    val eventShowMyTravel = SingleLiveEvent<Void>()
    val eventRouteTravel = SingleLiveEvent<Pair<Location?, List<TravelWayPoint>>>()
    val eventShowRequireLogin = SingleLiveEvent<Void>()

    val eventShowRequireNavigationParking = SingleLiveEvent<TravelWayPoint>()
    val eventShowSelectTravelToAdd = SingleLiveEvent<TravelWayPoint>()

    val isCouponDetailVisible = MutableLiveData<ShouCoupon?>(null)
    val isPlaceDetailVisible = MutableLiveData<ShouPlace?>(null)
    val isCouponListVisible = MutableLiveData<Boolean>(false)
    val isVoiceStoreListVisible = MutableLiveData<Boolean>(false)
    val isPlaceListVisible = MutableLiveData<Boolean>(false)
    val isMyTravelVisible = MutableLiveData<Boolean>(false)
    val isTopMenuContentVisible = MutableLiveData(false)

    val isShowShortCut = MediatorLiveData<Boolean>()

    var pendingAction: Runnable? = null

    init {
        displayCouponPlaceBounds.addSource(displayCoupon) {
            if (currentDataSource.value == DataSource.Location) return@addSource
            displayCouponPlaceBounds.value =
                updateDisplayBounds(it, displayPlace.value ?: return@addSource)
        }

        displayCouponPlaceBounds.addSource(displayPlace) {
            if (currentDataSource.value == DataSource.Location) return@addSource
            displayCouponPlaceBounds.value =
                updateDisplayBounds(displayCoupon.value ?: return@addSource, it)
        }

        isShowShortCut.value = true
        val observer = Observer<Boolean> {
            //以下任一畫面顯示時就不顯示主畫面的coupon、store(place)快捷鍵
            isShowShortCut.value =
                !(isCouponDetailVisible.value != null || isPlaceDetailVisible.value != null
                        || isCouponListVisible.value!!
                        || isPlaceListVisible.value!!
                        || isMyTravelVisible.value!!
                        || isVoiceStoreListVisible.value!! )
        }
        isShowShortCut.addSource(isCouponDetailVisible.map { it != null }, observer)
        isShowShortCut.addSource(isPlaceDetailVisible.map { it != null }, observer)
        isShowShortCut.addSource(isCouponListVisible, observer)
        isShowShortCut.addSource(isVoiceStoreListVisible, observer)
        isShowShortCut.addSource(isPlaceListVisible, observer)
        isShowShortCut.addSource(isMyTravelVisible, observer)
    }

    private fun updateDisplayBounds(
        displayCoupon: List<ShouCoupon>,
        displayPlace: List<ShouPlace>
    ): List<LatLng> {
        val couponLatLngs = displayCoupon.map { LatLng(it.place.lat, it.place.lng) }
        val placeLatLngs = displayPlace.map { LatLng(it.lat, it.lng) }

        if (couponLatLngs.isEmpty() && placeLatLngs.isEmpty()) {

            (currentDataSource.value as? DataSource.Area)?.let {
                return listOf(LatLng(it.cityTown.lat, it.cityTown.lng))
            }

            (currentDataSource.value as? DataSource.Dest)?.let {
                return listOf(LatLng(it.cityTown.lat, it.cityTown.lng))
            }
        }

        return couponLatLngs + placeLatLngs
    }

    fun clickCoupon(order: Int) {
        val clickedCoupon = displayCoupon.value?.getOrNull(order) ?: return
        clickCoupon(clickedCoupon)
    }

    fun clickCoupon(coupon: ShouCoupon) = viewModelScope.launch(genericExceptionHandler) {
        if (userRepository.isCarAppLogin()) {
            eventShowCouponDetail.value = coupon
        } else {
            pendingAction = Runnable {
                eventShowCouponDetail.value = coupon
            }
            eventShowRequireLogin.call()
        }
    }

    fun clickCouponMore() {
        eventShowCouponList.call()
    }

    fun clickPlace(order: Int) {
        val clickedPlace = displayPlace.value?.getOrNull(order) ?: return
        clickPlace(clickedPlace)
    }

    fun clickPlace(place: ShouPlace) = viewModelScope.launch(genericExceptionHandler) {
        if (userRepository.isCarAppLogin()) {
            eventShowPlaceDetail.value = place
        } else {
            pendingAction = Runnable {
                eventShowPlaceDetail.value = place
            }
            eventShowRequireLogin.call()
        }
    }

    fun clickPlaceMore() {
        eventShowPlaceList.call()
    }

    fun showPlaceDetail(placeId: Int) {
        val list = displayPlace.value ?: return
        val place = list.firstOrNull { it.placeId == placeId }
        place?.let { clickPlace(it) }
    }

    fun showCouponDetail(placeId: Int) {
        val list = displayCoupon.value ?: return
        val coupons = list.filter { it.place.placeId == placeId }
        if (coupons.isEmpty()) {
            toast("查無優惠券")
            return
        }
        if (coupons.size == 1) {
            clickCoupon(coupons[0])
        } else {
            eventShowCouponsByPlaceId.value = coupons
        }
    }

    fun onCouponDetailShowing(coupon: ShouCoupon) {
        isCouponDetailVisible.value = coupon
    }

    fun onCouponDetailDisappear() {
        isCouponDetailVisible.value = null
        locationObserver.kick()
    }

    fun onPlaceDetailShowing(place: ShouPlace) {
        isPlaceDetailVisible.value = place
    }

    fun onPlaceDetailDisappear() {
        isPlaceDetailVisible.value = null
        locationObserver.kick()
    }

    fun onRequireLoginResultNoLogin() {
        pendingAction?.run()
    }

    fun isCouponInFavList(order: Int):Boolean{
        val coupon = displayCoupon.value?.get(order) ?: return false
        return repository.isInFavoriteList(coupon)
    }

    fun isPlaceInFavList(order: Int):Boolean{
        val coupon = displayPlace.value?.get(order) ?: return false
        return repository.isInFavoriteList(coupon)
    }

    fun addFavorite(place: IShouPlace) = viewModelScope.launch(genericExceptionHandler) {
        if (repository.isInFavoriteList(place)){
            repository.deleteFavorite(place)
        }else{
            repository.addFavorite(place)
        }
    }

    fun addFavorite(coupon: IShouCoupon) = viewModelScope.launch(genericExceptionHandler) {
        if (repository.isInFavoriteList(coupon)){
            repository.deleteFavorite(coupon)
        }else{
            repository.addFavorite(coupon)
        }
    }

    fun addTravel(wayPoint: TravelWayPoint) {
        if (wayPoint.hasParking()) {
            eventShowRequireNavigationParking.value = wayPoint
        } else {
            addTravel(wayPoint, false)
        }
    }

    fun addTravel(wayPoint: TravelWayPoint, navigatePark: Boolean) {
        //是否要加入導航到停車場
        selectTravelToAdd(wayPoint.copyWithNavParking(navigatePark))
    }

    private fun selectTravelToAdd(wayPoint: TravelWayPoint) {
        eventShowSelectTravelToAdd.value = wayPoint
    }

    fun deleteTravel(wayPoint: TravelWayPoint) {
        repository.deleteTravel(wayPoint)
    }

    fun swapTravelOrder(fromPosition: Int, toPosition: Int) {
        repository.swapTravelOrder(fromPosition, toPosition)
    }

    fun clickMyTravel() {
        eventShowMyTravel.call()
    }

    fun startRouteTravel() {
        val currentLocation = currentLocation.value ?: return
        val currentTravel = currentTravel.value ?: return
        eventRouteTravel.value = Pair(currentLocation, currentTravel)
    }

    fun isTravelEmpty(): Boolean{
        return repository.isTravelEmpty()
    }

    fun clickTopMenu() {
        isTopMenuContentVisible.value = !isTopMenuContentVisible.value!!
    }

    fun clickSelectDataSource(sourceOption: DataSourceOption) {
        eventShowSelectDataSource.value = sourceOption
        isTopMenuContentVisible.value = false
    }

    fun clickEditDataSource() {
        eventShowSelectDataSource.value = eventShowSelectDataSource.value
        isTopMenuContentVisible.value = false
    }

    fun setDataSource(dataSource: DataSource) {
        currentDataSource.value = dataSource
        if (dataSource == DataSource.Location) {
            currentLocation.value?.let { location ->
                val latlng = LatLng(location.latitude, location.longitude)
                displayCouponPlaceBounds.value = listOf(latlng)
            }
        }
    }

    suspend fun saveTravel(travelName: String): Int {
        val travelList = currentTravel.value ?: return 0
        return repository.addFavoriteTravel(travelName, travelList)
    }

    fun clearCurrentTravel(){
        repository.clearCurrentTravel()
    }

    fun newCouponNotificationHandled() {
        repository.newCouponNotificationHandled()
    }

    fun checkNewCoupon() = viewModelScope.launch(genericExceptionHandler) {
        val couponId = notificationNewCoupon.value?.couponId ?: return@launch
        val placeId = notificationNewCoupon.value?.vendorId ?: return@launch
        val location = locationObserver.getLastLocation() ?: return@launch

        val placeDetail = repository.getPlaceDetail(placeId, location.latitude, location.longitude)
        val couponDetail = repository.getCouponDetail(couponId, placeDetail)

        eventShowCouponDetail.value = couponDetail.toShouCoupon()
    }

    fun likeCoupon(order: Int) = viewModelScope.launch(genericExceptionHandler) {
        displayCoupon.value?.get(order)?.let {
            showLoading(true)
            if (favCouponIds.value?.contains(it.couponId) == true){
                repository.deleteFavorite(it)
            }else{
                repository.addFavorite(it)
            }
            showLoading(false)
        }
    }

    fun likePlace(order: Int) = viewModelScope.launch(genericExceptionHandler) {
        displayPlace.value?.get(order)?.let {
            showLoading(true)
            if (favPlaceIds.value?.contains(it.placeId) == true){
                repository.deleteFavorite(it)
            }else{
                repository.addFavorite(it)
            }
            showLoading(false)
        }
    }

}