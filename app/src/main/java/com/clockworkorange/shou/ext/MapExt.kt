package com.clockworkorange.shou.ext

import android.location.Location
import com.clockworkorange.repository.domain.ShouCoupon
import com.clockworkorange.repository.domain.ShouPlace
import com.clockworkorange.repository.domain.VoiceStore
import com.clockworkorange.shou.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker

fun GoogleMap.centerCamera(location: Location, zoomLevel: Float = 13f, withAnim: Boolean = true){
    val update = CameraUpdateFactory.newLatLngZoom(location.toLatLng(), zoomLevel)
    if (withAnim){
        animateCamera(update)
    }else{
        moveCamera(update)
    }
}

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)


fun GoogleMap.addCouponMarker(coupon: ShouCoupon, isHighlight: Boolean = false): Marker{
    return addMarker {
        position(LatLng(coupon.place.lat, coupon.place.lng))
        title(coupon.name)
        val icResId = if (isHighlight) R.drawable.ic_marker_coupon_highlight else R.drawable.ic_marker_coupon
        icon(BitmapDescriptorFactory.fromResource(icResId))
    }
}

fun GoogleMap.addPlaceMarker(place: ShouPlace, isHighlight: Boolean = false): Marker{
    return addMarker {
        position(LatLng(place.lat, place.lng))
        title(place.name)
        val icResId = if (isHighlight) R.drawable.ic_marker_place_highlight else R.drawable.ic_marker_place
        icon(BitmapDescriptorFactory.fromResource(icResId))
    }
}

fun GoogleMap.addVoiceStoreMarker(store: VoiceStore, isHighlight: Boolean = false): Marker{
    return addMarker {
        position(LatLng(store.lat, store.lng))
        title(store.name)
        val icResId = if (isHighlight) R.drawable.ic_marker_coupon_highlight else R.drawable.ic_marker_coupon
        icon(BitmapDescriptorFactory.fromResource(icResId))
    }
}

fun GoogleMap.addMeMarker(location: Location): Marker{
    return addMarker {
        position(location.toLatLng())
        title("")
        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_me))
    }
}

fun GoogleMap.showTaiwan(){

    val centerLocation = Location("").apply {
        latitude = 23.973931274035273
        longitude = 120.9796931908982
    }

    centerCamera(centerLocation, 8f, false)
}