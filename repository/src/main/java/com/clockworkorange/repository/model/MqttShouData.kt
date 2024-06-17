package com.clockworkorange.repository.model
import com.google.gson.annotations.SerializedName

/**
 * ```
 * {
 *   "time": "2021-07-13 14:47:13(UTC時間)",
 *   "lat": 24.187067,
 *   "lon": 120.663184,
 *   "triggers": [
 *     {
 *       "id": "v1(trigger_id)",
 *       "data": {
 *         "store_id": 1,
 *         "store_name": "漢來海港",
 *         "category_id": 64
 *       }
 *     },
 *     {
 *       "id": "c1(trigger_id)",
 *       "data": {
 *         "store_id": 1,
 *         "store_name": "漢來海港",
 *         "coupon_id": 1,
 *         "coupon_name": "九折券",
 *         "category_id": 64
 *       }
 *     }
 *   ]
 * }
 * ```
 */
data class MqttShouData(
    @SerializedName("time")
    val time: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("triggers")
    val triggers: List<Trigger>
)

data class Trigger(
    @SerializedName("id")
    val id: String,
    @SerializedName("data")
    val triggerData: TriggerData
)

data class TriggerData(
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("coupon_id")
    val couponId: Int?,
    @SerializedName("coupon_name")
    val couponName: String?,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
){

    fun isCoupon(): Boolean{
        return couponId != null
    }

}