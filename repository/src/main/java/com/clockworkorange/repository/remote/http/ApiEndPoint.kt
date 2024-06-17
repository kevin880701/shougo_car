package com.clockworkorange.repository.remote.http

object ApiEndPoint {

    const val BASE_URL = "https://shougo.cwoiot.com/shougo/"

    const val IsCarAppLogin = "v1/user/islogin"
    const val LogoutCar = "v1/user2/carLogout"
    const val GetUserInfo = "v1/user/info"
    const val RefreshToken = "v1/user/token/refresh"
    const val GetCityTown = "v1/search/zip/sync/{ver}"
    const val GetRecommendStoreByLatLng = "v1/search/store/recommend/{limit}"
    const val GetRecommendStoreByAddress = "v1/search/store/recommend/addr/{limit}"
    const val GetNearCouponByAddress = "v1/search/coupon/near/addr/{limit}"

    const val GetStoreDetail = "v1/search/store/dtl_desc"
    const val GetCouponDetail = "v1/search/coupon/dtl"

    const val GetFavoriteStoreList = "v1/favorites/store"
    const val GetFavoriteCouponList = "v1/favorites/coupon"
    const val GetFavoriteTravelList = "v1/favorites/waypoint"
    const val GetFavoriteTravelDetail = "v1/favorites/waypoint/dtl"

    const val AddFavorite = "v1/favorites/add"
    const val AddFavoriteTravel = "v1/favorites/waypoint/add"
    const val UpdateFavoriteTravel = "v1/favorites/waypoint/upd"

    const val DeleteFavorite = "v1/favorites/del"
    const val DeleteFavoriteTravel = "v1/favorites/waypoint/del"

    const val SearchCategory = "v1/search/category/{limit}"

    const val UploadFCMToken = "v1/user/upd/device/firebase_token"

    const val RegisterDeviceId = "v1/user/addcar"
}