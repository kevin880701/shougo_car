package com.clockworkorange.repository.remote.http

import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.core.content.edit
import com.clockworkorange.repository.domain.IShouCoupon
import com.clockworkorange.repository.domain.IShouPlace
import com.clockworkorange.repository.domain.TravelWayPoint
import com.clockworkorange.repository.model.AddFavoriteTravelBody
import com.clockworkorange.repository.model.ApiFavoriteCoupon
import com.clockworkorange.repository.model.ApiSearchCategory
import com.clockworkorange.repository.model.ApiShouCoupon
import com.clockworkorange.repository.model.ApiShouCouponDetail
import com.clockworkorange.repository.model.ApiShouStore
import com.clockworkorange.repository.model.ApiShouStoreDetail
import com.clockworkorange.repository.model.ApiTravel
import com.clockworkorange.repository.model.ApiWayPoint
import com.clockworkorange.repository.model.GenericErrorResponse
import com.clockworkorange.repository.model.GetCityTownResponse
import com.clockworkorange.repository.model.GetFavoriteTravelDetailResponse
import com.clockworkorange.repository.model.RefreshTokenBody
import com.clockworkorange.repository.model.SearchedItem
import com.clockworkorange.repository.model.UpdateFavoriteTravelBody
import com.clockworkorange.repository.model.UserInfo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.net.ConnectException
import java.util.UUID

class ShouServiceWrapper(private val shouService: ShouService, private val sharedPreferences: SharedPreferences, private val gson: Gson) {

    companion object{
        private const val KEY_TOKEN = "KEY_TOKEN"
        private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
        private const val KEY_DEVICE_ID = "KEY_DEVICE_ID"
        private const val KEY_CITY_TOWN = "KEY_CITY_TOWN"
        private const val NotLoginToken = "cwot1_sz3rJrdkvjsANC3eqNsDn7eh7Jek0OBRqz5k7Uw8bhwkmAXUJQIK190vNM"
        private const val RequestLimit = 20
        private const val KEY_IS_ID_REGISTER = "KEY_IS_ID_REGISTER"
    }

    private var cachedDeviceId: String? = null
    private var cachedToken: String? = null
    private var cachedCategory: List<ApiSearchCategory>? = null

    fun getDeviceId(): String {
        cachedDeviceId?.let { return it }

        var deviceId = sharedPreferences.getString(KEY_DEVICE_ID, "")
        Log.v("QQQQQQQQQQQQQQQQQ","" + deviceId)
        Log.v("QQQQQQQQQQQQQQQQQ","" + NotLoginToken)
//        deviceId = UUID.randomUUID().toString()
//        sharedPreferences.edit(commit = true){
//            putString(KEY_DEVICE_ID, deviceId)
//        }
//        GlobalScope.launch(Dispatchers.IO) {
//            registerDeviceId(deviceId!!)
//        }

        if (deviceId.isNullOrEmpty()){
            deviceId = UUID.randomUUID().toString()
            sharedPreferences.edit(commit = true){
                putString(KEY_DEVICE_ID, deviceId)
            }

            //todo use own coroutinesScope
//            val isIdRegister = sharedPreferences.getBoolean(KEY_IS_ID_REGISTER, false)
//            if (!isIdRegister){
//                GlobalScope.launch(Dispatchers.IO) {
//                    val registerSuccess = registerDeviceId(deviceId)
//                    sharedPreferences.edit(true){
//                        putBoolean(KEY_IS_ID_REGISTER, registerSuccess)
//                    }
//                }
//            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            registerDeviceId(deviceId)
        }
        cachedDeviceId = deviceId
        return deviceId
    }

    private fun getToken(): String {
        cachedToken?.let { return it }
        cachedToken = sharedPreferences.getString(KEY_TOKEN, "")
        return cachedToken!!
    }

    private suspend fun refreshToken(){
        val refreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, "")!!
        val response = shouService.refreshToken(RefreshTokenBody(refreshToken))
        sharedPreferences.edit {
            putString(KEY_TOKEN, response.token)
            putString(KEY_REFRESH_TOKEN, response.refreshToken)
        }

        cachedToken = response.token
    }

    suspend fun isCarAppLogin(): Boolean{
        return try {
            val response = shouService.isCarAppLogin(getDeviceId())

            sharedPreferences.edit {
                putString(KEY_TOKEN, response.token)
                putString(KEY_REFRESH_TOKEN, response.refreshToken)
            }
            Log.v("AAAAAAAAAAAAAC","" + getDeviceId())
            Log.v("AAAAAAAAAAAAAC","" + response.token)
            Log.v("AAAAAAAAAAAAAC","" + response.refreshToken)

            cachedToken = response.token
            true
        }catch (e: HttpException) {
            false
        }catch (e: ConnectException){
            throw e
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserInfo(): UserInfo= refreshWhenTokenExpired{
        return@refreshWhenTokenExpired shouService.getUserInfo(getToken())
    }

    private suspend fun unbindUserAndCar() = refreshWhenTokenExpired{
        return@refreshWhenTokenExpired shouService.logoutCar(getToken(), mapOf("device_id" to getDeviceId()))
    }

    suspend fun logout(): Boolean {
        try {
            // 可能抛出异常的操作
            if (unbindUserAndCar()) {
                sharedPreferences.edit(commit = true){
                    remove(KEY_TOKEN)
                    remove(KEY_REFRESH_TOKEN)
                }
                cachedDeviceId = null
                cachedCategory = null
                cachedToken = null
                cachedFavoriteCoupon.clear()
                cachedFavoriteStore.clear()
                cachedStoreDetailData.clear()
                return true
            }
            return false
        } catch (e: Exception) {
            // 捕获并处理异常
            // 避免在这里抛出新异常
            Log.v("AAAAAAAAAAAAAAAAA" ,"" +  e)
            return false
        }

    }

    suspend fun getCityTown(): GetCityTownResponse {
        val gson = Gson()
        val savedCityTownJsonString = sharedPreferences.getString(KEY_CITY_TOWN, "")
        if (savedCityTownJsonString.isNullOrEmpty()){
            //沒有存過資料
            val response = shouService.getCityTown(NotLoginToken, 0)
            sharedPreferences.edit {
                putString(KEY_CITY_TOWN, gson.toJson(response))
            }
            return response
        }

        val savedCityTownResponse = gson.fromJson(savedCityTownJsonString, GetCityTownResponse::class.java)
        val serverResponse = shouService.getCityTown(NotLoginToken, savedCityTownResponse.ver)
        //如果版本號一樣，表示資料沒有變更
        //返回儲存的資料即可
        if (serverResponse.ver == savedCityTownResponse.ver){
            return savedCityTownResponse
        }
        //版本號不一樣，更新儲存的資料
        sharedPreferences.edit {
            putString(KEY_CITY_TOWN, gson.toJson(serverResponse))
        }

        return serverResponse
    }

    suspend fun getStoreByLatLng(location: Location): List<ApiShouStore>{
        return shouService.getStoreByLatLng(NotLoginToken, RequestLimit, location.latitude, location.longitude)
    }

    suspend fun getStoreByAddress(address: String): List<ApiShouStore>{
        return shouService.getStoreByAddress(NotLoginToken, RequestLimit, address)
    }

    suspend fun getCouponByAddress(address: String): List<ApiShouCoupon>{
        return shouService.getCouponByAddress(NotLoginToken, RequestLimit, address)
    }

    private val cachedStoreDetailData = HashMap<Int, ApiShouStoreDetail>()
    suspend fun getStoreDetail(storeId: Int): ApiShouStoreDetail{
        if (cachedStoreDetailData.containsKey(storeId)){
            return cachedStoreDetailData[storeId]!!
        }
        val data = shouService.getStoreDetail(NotLoginToken, storeId)
        cachedStoreDetailData[storeId] = data
        return cachedStoreDetailData[storeId]!!
    }

    suspend fun getStoreDetail(id: Int, lat: Double, lng: Double): ApiShouStoreDetail = refreshWhenTokenExpired {
        return@refreshWhenTokenExpired shouService.getStoreDetailWithLatLng(getToken(), id, lat, lng)
    }

    suspend fun getCouponDetail(couponId: Int): ApiShouCouponDetail{
        return shouService.getCouponDetail(NotLoginToken, couponId)
    }

    private val cachedFavoriteStore: MutableList<ApiShouStoreDetail> = mutableListOf()
    private var isFavoriteStoreRealEmpty: Boolean = false

    suspend fun getFavoriteStoreList(): List<ApiShouStoreDetail> = refreshWhenTokenExpired{

        if (getToken().isEmpty()) return@refreshWhenTokenExpired emptyList()

        if (cachedFavoriteStore.isEmpty() && !isFavoriteStoreRealEmpty){
            val data = shouService.getFavoriteStoreList(getToken())
            cachedFavoriteStore.addAll(data)
            if (data.isEmpty()) isFavoriteStoreRealEmpty = true
        }
        return@refreshWhenTokenExpired cachedFavoriteStore.toList()
    }

    private val cachedFavoriteCoupon: MutableList<ApiFavoriteCoupon> = mutableListOf()
    private var isFavoriteCouponRealEmpty: Boolean = false

    suspend fun getFavoriteCouponList(): List<ApiFavoriteCoupon> = refreshWhenTokenExpired{

        if (getToken().isEmpty()) return@refreshWhenTokenExpired emptyList()

        if (cachedFavoriteCoupon.isEmpty() && !isFavoriteCouponRealEmpty){
            val data = shouService.getFavoriteCouponList(getToken())
            cachedFavoriteCoupon.addAll(data)
            if (data.isEmpty()) isFavoriteCouponRealEmpty = true
        }
        return@refreshWhenTokenExpired cachedFavoriteCoupon.toList()
    }

    suspend fun addCouponToFavorite(couponId: Int): Boolean = refreshWhenTokenExpired{

        if (getToken().isEmpty()) return@refreshWhenTokenExpired false

        val data = mapOf(
            "id" to couponId.toString(),
            "type" to "0"
        )
        val res = shouService.addFavorite(getToken(), data)
        cachedFavoriteCoupon.clear()
        isFavoriteCouponRealEmpty = false
        cachedFavoriteStore.clear()
        isFavoriteStoreRealEmpty = false
        return@refreshWhenTokenExpired res
    }

    suspend fun addStoreToFavorite(storeId: Int): Boolean = refreshWhenTokenExpired{

        if (getToken().isEmpty()) return@refreshWhenTokenExpired false

        val data = mapOf(
            "id" to storeId.toString(),
            "type" to "1"
        )
        val res = shouService.addFavorite(getToken(), data)
        cachedFavoriteStore.clear()
        isFavoriteStoreRealEmpty = false
        return@refreshWhenTokenExpired res
    }

    suspend fun deleteFavoriteCoupon(shouCoupon: IShouCoupon): Boolean = refreshWhenTokenExpired {

        if (getToken().isEmpty()) return@refreshWhenTokenExpired false

        val data = mutableMapOf(
            "id" to shouCoupon.couponId,
            "type" to 0
        )
        cachedFavoriteCoupon.clear()
        isFavoriteCouponRealEmpty = false
        return@refreshWhenTokenExpired shouService.deleteFavorite(getToken(), data)
    }

    suspend fun deleteFavoritePlace(shouPlace: IShouPlace): Boolean = refreshWhenTokenExpired {

        if (getToken().isEmpty()) return@refreshWhenTokenExpired false

        val data = mutableMapOf(
            "id" to shouPlace.placeId,
            "type" to 1
        )
        cachedFavoriteStore.clear()
        isFavoriteStoreRealEmpty = false
        return@refreshWhenTokenExpired shouService.deleteFavorite(getToken(), data)
    }

    fun clearFavoriteCache() {
        cachedFavoriteStore.clear()
        isFavoriteStoreRealEmpty = false
        cachedFavoriteCoupon.clear()
        isFavoriteCouponRealEmpty = false
    }

    private suspend fun <T> refreshWhenTokenExpired(block: suspend () -> T): T{
        return try {
            block()
        }catch (e: HttpException){
            if (e.code() == 401){
                refreshToken()
                return block()
            }else{
                val body = e.response()?.errorBody()?.charStream()?.readText()
                if (body.isNullOrEmpty()){
                    throw e
                }else{
                    val errorResponse:GenericErrorResponse = gson.fromJson(body, GenericErrorResponse::class.java)
                    throw ApiException(errorResponse)
                }
            }
        }
    }

    suspend fun addFavoriteTravel(name: String, wayPoints: List<TravelWayPoint>): Int = refreshWhenTokenExpired{

        if (getToken().isEmpty()) return@refreshWhenTokenExpired -1

        val apiWayPoints = mutableListOf<ApiWayPoint>()

        wayPoints.forEachIndexed { index, travelWayPoint ->

            if (travelWayPoint.isNavParking){
                apiWayPoints.add(ApiWayPoint.createParkingWayPoint(travelWayPoint.placeId, index))
                return@forEachIndexed
            }

            when(travelWayPoint){
                is TravelWayPoint.TravelCoupon -> {
                    apiWayPoints.add(ApiWayPoint.createCouponWayPoint(travelWayPoint.couponId, travelWayPoint.placeId, index))
                }
                is TravelWayPoint.TravelPlace -> {
                    apiWayPoints.add(ApiWayPoint.createStoreWayPoint(travelWayPoint.placeId, index))

                }
            }
        }

        val data = AddFavoriteTravelBody(name, apiWayPoints)
        return@refreshWhenTokenExpired shouService.addFavoriteTravel(getToken(), data)
    }

    suspend fun updateFavoriteTravel(id:Int, name: String, wayPoints: List<TravelWayPoint>): Boolean = refreshWhenTokenExpired {
        if (getToken().isEmpty()) return@refreshWhenTokenExpired false

        val apiWayPoints = mutableListOf<ApiWayPoint>()

        wayPoints.forEachIndexed { index, travelWayPoint ->

            if (travelWayPoint.isNavParking){
                apiWayPoints.add(ApiWayPoint.createParkingWayPoint(travelWayPoint.placeId, index))
                return@forEachIndexed
            }

            when(travelWayPoint){
                is TravelWayPoint.TravelCoupon -> {
                    apiWayPoints.add(ApiWayPoint.createCouponWayPoint(travelWayPoint.couponId, travelWayPoint.placeId, index))
                }
                is TravelWayPoint.TravelPlace -> {
                    apiWayPoints.add(ApiWayPoint.createStoreWayPoint(travelWayPoint.placeId, index))

                }
            }
        }

        val data = UpdateFavoriteTravelBody(id, name, apiWayPoints)
        return@refreshWhenTokenExpired shouService.updateFavoriteTravel(getToken(), data)
    }

    suspend fun getFavoriteTravelList(): List<ApiTravel> = refreshWhenTokenExpired{
        if (getToken().isEmpty()) return@refreshWhenTokenExpired emptyList()
        return@refreshWhenTokenExpired shouService.getFavoriteTravelList(getToken())
    }

    suspend fun getFavoriteTravelDetail(travelId: Int, lat: Double?, lng: Double?): GetFavoriteTravelDetailResponse = refreshWhenTokenExpired {
        return@refreshWhenTokenExpired shouService.getFavoriteTravelDetail(getToken(), travelId, lat, lng)
    }

    suspend fun deleteFavoriteTravel(travelId: Int): Boolean = refreshWhenTokenExpired {
        if (getToken().isEmpty()) return@refreshWhenTokenExpired false
        val data = mutableMapOf("waypoint_id" to travelId)
        return@refreshWhenTokenExpired shouService.deleteFavoriteTravel(getToken(), data)
    }

    suspend fun getCategoryNameMap(): List<ApiSearchCategory>{
        cachedCategory?.let { return it }
        cachedCategory = shouService.getCategory(NotLoginToken, 0)
        return cachedCategory!!
    }

    suspend fun uploadFCMToken(fcmToken: String): Boolean = refreshWhenTokenExpired {
        if (getToken().isEmpty()) return@refreshWhenTokenExpired false
        val data = mapOf(
            "firebase_token" to fcmToken,
            "device_id" to getDeviceId()
        )
        return@refreshWhenTokenExpired shouService.uploadFCMToken(getToken(), data)
    }

    private suspend fun registerDeviceId(id: String): Boolean{
        val data = mapOf(
            "devid" to id,
            "name" to id
        )
        return shouService.registerDeviceId(NotLoginToken, data)
    }

    suspend fun getStoreWithTag(lat: Double, lng: Double, category: String): List<SearchedItem>{
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("latitude", lat.toString())
            .addFormDataPart("longitude", lng.toString())
            .addFormDataPart("TagStrings", category)
            .build()
        return shouService.searchTag(requestBody).item
    }

}