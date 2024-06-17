package com.clockworkorange.repository

import com.clockworkorange.repository.domain.*
import com.clockworkorange.repository.model.*
import com.clockworkorange.repository.remote.http.ShouServiceWrapper
import kotlin.math.*

fun SearchedItem.toVoiceStore(): VoiceStore {
    return VoiceStore(
        id = id,
        name = name,
        logoUrl = logo,
        webUrl = webUrl,
        lat = latitude.toDouble(),
        lng = longitude.toDouble(),
        distance = distance.toInt()
    )
}

suspend fun ApiShouCoupon.toShouCoupon(serviceWrapper: ShouServiceWrapper): ShouCoupon{
    val categoryName = serviceWrapper.getCategoryNameMap().find { it.categoryId == categoryId }?.name ?: "unknown"
    return ShouCoupon(
        couponId,
        name,
        profileImage,
        ShouPlace(
            vendorId,
            storeName,
            lat,
            lng,
            "",
            categoryId,
            categoryName,
            distance ?: 0.0
        )
    )
}

suspend fun ApiShouStore.toShouPlace(serviceWrapper: ShouServiceWrapper):ShouPlace{
    val categoryName = serviceWrapper.getCategoryNameMap().find { it.categoryId == categoryId }?.name ?: "unknown"

    return ShouPlace(
        vendorId,
        storeName,
        lat,
        lng,
        logo,
        categoryId,
        categoryName,
        distance ?: 0.0
    )
}

fun ApiShouCouponDetail.toShouCouponDetail(placeDetail: ShouPlaceDetail): ShouCouponDetail{

    return ShouCouponDetail(
        couponId,
        name,
        profileImage,
        placeDetail,
        description,
        dateStart,
        dateEnd
    )
}

fun ApiShouStoreDetail.toShouPlaceDetail(distance: Double = 0.0): ShouPlaceDetail{
    val data = ShouPlaceDetail(
        vendorId,
        storeName,
        lat,
        lng,
        logo,
        categoryId,
        categoryName,
        distance,
        description,
        address,
        email,
        telephone,
        parkingAddress,
        parkingLat,
        parkingLng,
        star,
        zone,
        desc ?: emptyList()
    )

    if (this.distance != null){
        data.distance = this.distance
    }
    return data
}

fun ApiTravel.toShouTravel(): ShouTravel{
    return ShouTravel(
        waypointId,
        name,
        dateAdded
    )
}

suspend fun ApiWaypointDetail.toTravelWayPoint(shouService: ShouServiceWrapper): TravelWayPoint{
    return when(type){
        0 -> TravelWayPoint.TravelCoupon(
            shouService.getCouponDetail(this.couponId).toShouCouponDetail(shouService.getStoreDetail(vendorId).toShouPlaceDetail(distance))
        )

        1 -> TravelWayPoint.TravelPlace(
            shouService.getStoreDetail(vendorId).toShouPlaceDetail(distance)
        )

        2 -> TravelWayPoint.TravelPlace(
            shouService.getStoreDetail(vendorId).toShouPlaceDetail(distance), true
        )

        else -> throw IllegalArgumentException("type should be 0, 1, 2, 3")
    }

}

suspend fun GetFavoriteTravelDetailResponse.toShouTravelDetail(shouService: ShouServiceWrapper): ShouTravelDetail{
    return ShouTravelDetail(
        waypointId,
        name,
        dateAdded,
        apiWaypointDetails.map { it.toTravelWayPoint(shouService) }
    )
}

fun ApiSearchCategory.toShouCategory(): ShouCategory{
    return ShouCategory(categoryId, image, name)
}

fun ApiFavoriteCoupon.toShouCouponDetail():ShouCouponDetail{
    val placeDetail = ShouPlaceDetail(
        vendorId,
        storeName,
        lat,
        lng,
        storeLogo,
        categoryId,
        categoryName,
        0.0,
        description,
        address,
        email,
        telephone,
        parkingAddress,
        parkingLat,
        parkingLng,
        star,
        zone,
        emptyList()
    )
    return ShouCouponDetail(
        couponId,
        name,
        profileImage,
        placeDetail,
        description,
        dateStart,
        dateEnd
    )
}

fun ShouPlaceDetail.toShouPlace(): ShouPlace{
    return ShouPlace(
        placeId, name, lat, lng, image, categoryId, categoryName, distance
    )
}

fun ShouCouponDetail.toShouCoupon(): ShouCoupon{
    return ShouCoupon(
        couponId,
        name,
        image,
        place.toShouPlace()
    )
}

fun calcDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val R = 6371 // Radius of the earth

    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lng2 - lng1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R * c

    distance = distance.pow(2.0)

    return sqrt(distance)
}