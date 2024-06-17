package com.clockworkorange.repository.domain

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.clockworkorange.repository.calcDistance
import com.clockworkorange.repository.model.StoreDesc
import kotlinx.parcelize.Parcelize


interface IShouPlace {
    val placeId: Int
    val name: String
    val lat: Double
    val lng: Double
    val image: String
    val categoryId: Int
    val categoryName: String
    var distance: Double

    val qrCodeContent: String
        get() = "{\"id\":$placeId, \"type\":1}"

    fun calDistanceFromLocation(location: Location?){
        location?.let {
            distance = calcDistance(lat, lng, location.latitude, location.longitude)
        }
    }
}

data class ShouPlace(
    override val placeId: Int,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
    override val image: String,
    override val categoryId: Int = -1,
    override val categoryName: String,
    override var distance: Double = 0.0,
): IShouPlace, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(placeId)
        parcel.writeString(name)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(image)
        parcel.writeInt(categoryId)
        parcel.writeDouble(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShouPlace> {
        override fun createFromParcel(parcel: Parcel): ShouPlace {
            return ShouPlace(parcel)
        }

        override fun newArray(size: Int): Array<ShouPlace?> {
            return arrayOfNulls(size)
        }
    }

}

@Parcelize
data class ShouPlaceDetail(
    override val placeId: Int,
    override val name: String,
    override val lat: Double,
    override val lng: Double,
    override val image: String,
    override val categoryId: Int = -1,
    override val categoryName: String,
    override var distance: Double = 0.0,

    val desc: String,
    val address: String,
    val email: String,
    val telephone: String,
    val parkingAddress: String,
    val parkingLat: Double,
    val parkingLng: Double,
    val star: Double,
    val city: String,
    val detailDesc: List<StoreDesc>
): IShouPlace, Parcelable {
    fun hasParking(): Boolean = parkingAddress.isNotEmpty() || (parkingLat != 0.0 && parkingLng != 0.0)
}

