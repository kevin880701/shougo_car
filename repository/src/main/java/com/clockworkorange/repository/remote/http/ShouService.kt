package com.clockworkorange.repository.remote.http

import com.clockworkorange.repository.BuildConfig
import com.clockworkorange.repository.model.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ShouService {

    @GET(ApiEndPoint.IsCarAppLogin)
    suspend fun isCarAppLogin(@Query("device_id") deviceId: String): LoginResponse

    @POST(ApiEndPoint.LogoutCar)
    suspend fun logoutCar(@Header("Authorization") token: String, @Body data: Map<String, String>): Boolean

    @GET(ApiEndPoint.GetUserInfo)
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfo

    @POST(ApiEndPoint.RefreshToken)
    suspend fun refreshToken(@Body body: RefreshTokenBody): LoginResponse

    @GET(ApiEndPoint.GetCityTown)
    suspend fun getCityTown(@Header("Authorization") token: String, @Path("ver") ver: Int): GetCityTownResponse

    @GET(ApiEndPoint.GetRecommendStoreByLatLng)
    suspend fun getStoreByLatLng(
        @Header("Authorization") token: String,
        @Path("limit") limit: Int,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): List<ApiShouStore>

    @GET(ApiEndPoint.GetRecommendStoreByAddress)
    suspend fun getStoreByAddress(
        @Header("Authorization") token: String,
        @Path("limit") limit: Int,
        @Query("address") address: String
    ): List<ApiShouStore>

    @GET(ApiEndPoint.GetNearCouponByAddress)
    suspend fun getCouponByAddress(
        @Header("Authorization") token: String,
        @Path("limit") limit: Int,
        @Query("address") address: String
    ): List<ApiShouCoupon>

    @GET(ApiEndPoint.GetStoreDetail)
    suspend fun getStoreDetail(@Header("Authorization") token: String, @Query("id") id: Int): ApiShouStoreDetail

    @GET(ApiEndPoint.GetStoreDetail)
    suspend fun getStoreDetailWithLatLng(@Header("Authorization") token: String,
                                         @Query("id") id: Int,
                                         @Query("lat") lat: Double,
                                         @Query("lng") lng: Double
    ): ApiShouStoreDetail

    @GET(ApiEndPoint.GetCouponDetail)
    suspend fun getCouponDetail(@Header("Authorization") token: String, @Query("id") id: Int): ApiShouCouponDetail

    @GET(ApiEndPoint.GetFavoriteStoreList)
    suspend fun getFavoriteStoreList(@Header("Authorization") token: String): List<ApiShouStoreDetail>

    @GET(ApiEndPoint.GetFavoriteCouponList)
    suspend fun getFavoriteCouponList(@Header("Authorization") token: String): List<ApiFavoriteCoupon>

    @POST(ApiEndPoint.AddFavorite)
    suspend fun addFavorite(@Header("Authorization") token: String, @Body data: Map<String, String>): Boolean

    @POST(ApiEndPoint.AddFavoriteTravel)
    suspend fun addFavoriteTravel(@Header("Authorization") token: String, @Body data: AddFavoriteTravelBody): Int

    @PUT(ApiEndPoint.UpdateFavoriteTravel)
    suspend fun updateFavoriteTravel(@Header("Authorization") token: String, @Body data: UpdateFavoriteTravelBody): Boolean

    @GET(ApiEndPoint.GetFavoriteTravelList)
    suspend fun getFavoriteTravelList(@Header("Authorization") token: String): List<ApiTravel>

    @GET(ApiEndPoint.GetFavoriteTravelDetail)
    suspend fun getFavoriteTravelDetail(@Header("Authorization") token: String,
                                        @Query("waypoint_id") waypointId: Int,
                                        @Query("lat") lat: Double?,
                                        @Query("lng") lng: Double?
    ): GetFavoriteTravelDetailResponse

    @HTTP(method = "DELETE", path = ApiEndPoint.DeleteFavoriteTravel, hasBody = true)
    suspend fun deleteFavoriteTravel(@Header("Authorization") token: String, @Body data: Map<String, Int>): Boolean

    @HTTP(method = "DELETE", path = ApiEndPoint.DeleteFavorite, hasBody = true)
    suspend fun deleteFavorite(@Header("Authorization") token: String, @Body data: Map<String, Int>): Boolean

    @GET(ApiEndPoint.SearchCategory)
    suspend fun getCategory(@Header("Authorization") token: String, @Path("limit") limit: Int): List<ApiSearchCategory>

    @POST(ApiEndPoint.UploadFCMToken)
    suspend fun uploadFCMToken(@Header("Authorization") token: String, @Body data: Map<String, String>): Boolean

    @POST(ApiEndPoint.RegisterDeviceId)
    suspend fun registerDeviceId(@Header("Authorization") token: String, @Body data: Map<String, String>): Boolean

    // 顧問系統
    @POST("https://shougo.shoumaji.com/System/searchTag/StoreTag")
    suspend fun searchTag(@Body body: RequestBody): SearchTagResponse

    companion object{

        fun create(): ShouService {

            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG){
                val httpLogIntIterator = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
                builder.addInterceptor(httpLogIntIterator)
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiEndPoint.BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ShouService::class.java)
        }

    }

}