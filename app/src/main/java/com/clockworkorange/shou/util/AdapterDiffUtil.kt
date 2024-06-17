package com.clockworkorange.shou.util

import androidx.recyclerview.widget.DiffUtil
import com.clockworkorange.repository.domain.*


val IShouPlaceDiffUtil = object : DiffUtil.ItemCallback<IShouPlace>(){
    override fun areItemsTheSame(oldItem: IShouPlace, newItem: IShouPlace): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(oldItem: IShouPlace, newItem: IShouPlace): Boolean {
        return oldItem.placeId == newItem.placeId

    }
}

val IShouCouponDiffUtil = object : DiffUtil.ItemCallback<IShouCoupon>(){
    override fun areItemsTheSame(oldItem: IShouCoupon, newItem: IShouCoupon): Boolean {
        return oldItem.couponId == newItem.couponId
    }

    override fun areContentsTheSame(oldItem: IShouCoupon, newItem: IShouCoupon): Boolean {
        return oldItem.couponId == newItem.couponId
    }
}

val VoiceStoreDiffUtil = object : DiffUtil.ItemCallback<VoiceStore>(){
    override fun areItemsTheSame(oldItem: VoiceStore, newItem: VoiceStore): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VoiceStore, newItem: VoiceStore): Boolean {
        return oldItem.id == newItem.id
    }
}

val ShouTravelDiffUtil = object : DiffUtil.ItemCallback<ShouTravel>(){
    override fun areItemsTheSame(p0: ShouTravel, p1: ShouTravel): Boolean {
        return p0.travelId == p1.travelId
    }

    override fun areContentsTheSame(p0: ShouTravel, p1: ShouTravel): Boolean {
        return p0.travelId == p1.travelId
    }
}

val TravelWayPointDiffUtil = object : DiffUtil.ItemCallback<TravelWayPoint>(){
    override fun areItemsTheSame(p0: TravelWayPoint, p1: TravelWayPoint): Boolean {
        return p0.placeId == p1.placeId
    }

    override fun areContentsTheSame(p0: TravelWayPoint, p1: TravelWayPoint): Boolean {
        return p0.placeId == p1.placeId
    }
}
