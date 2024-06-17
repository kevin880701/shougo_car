package com.clockworkorange.repository.domain

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface IShouCoupon{
    val couponId: Int
    val name: String
    val image: String
    val place: IShouPlace

    val qrCodeContent: String
        get() = "{\"id\":$couponId, \"type\":0}"
    fun isCouponIdValid(): Boolean = couponId != INVALID_COUPON_ID

    companion object {
        const val INVALID_COUPON_ID = -1
    }
}

data class ShouCoupon(
    override val couponId: Int,
    override val name: String,
    override val image: String,
    override val place: ShouPlace,
    val webUrl: String = "", // 當資料內容來自顧問系統時填入 url https://shougo.shoumaji.com/System/searchTag/StoreTag
): IShouCoupon, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(ShouPlace::class.java.classLoader)!!,
        parcel.readString()!!,
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(couponId)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeParcelable(place, flags)
        parcel.writeString(webUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShouCoupon> {
        override fun createFromParcel(parcel: Parcel): ShouCoupon {
            return ShouCoupon(parcel)
        }

        override fun newArray(size: Int): Array<ShouCoupon?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class ShouCouponDetail(
    override val couponId: Int,
    override val name: String,
    override val image: String,
    override val place: ShouPlaceDetail,
    val desc: String,
    val dateStart: String,
    val dateEnd: String,
):IShouCoupon, Parcelable