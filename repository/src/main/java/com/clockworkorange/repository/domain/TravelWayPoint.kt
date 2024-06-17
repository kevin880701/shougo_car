package com.clockworkorange.repository.domain
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class TravelWayPoint(
    val placeId: Int,
    val couponId: Int = 0,
    val name: String,
    val lat: Double,
    val lng: Double,
    val distance: Double,
    open val isNavParking: Boolean,
    val parkingAddress: String = "",
    val parkingLat: Double = 0.0,
    val parkingLng: Double = 0.0
):Parcelable{

    @Parcelize
    class TravelPlace(val place: ShouPlaceDetail, override val isNavParking: Boolean = false) : TravelWayPoint(
        place.placeId,
        0,
        place.name,
        place.lat,
        place.lng,
        place.distance,
        isNavParking,
        place.parkingAddress,
        place.parkingLat,
        place.parkingLng)

    @Parcelize
    class TravelCoupon(val coupon: ShouCouponDetail, override val isNavParking: Boolean = false): TravelWayPoint(
        coupon.place.placeId,
        coupon.couponId,
        coupon.place.name,
        coupon.place.lat,
        coupon.place.lng,
        coupon.place.distance,
        isNavParking,
        coupon.place.parkingAddress,
        coupon.place.parkingLat,
        coupon.place.parkingLng)

    fun hasParking(): Boolean = parkingAddress.isNotEmpty() || (parkingLat != 0.0 && parkingLng != 0.0)

    fun copyWithNavParking(isNavParking: Boolean): TravelWayPoint{
        return when(this){
            is TravelCoupon -> TravelCoupon(coupon, isNavParking)
            is TravelPlace -> TravelPlace(place, isNavParking)
        }
    }
}
