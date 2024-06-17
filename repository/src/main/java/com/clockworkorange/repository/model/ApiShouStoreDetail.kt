package com.clockworkorange.repository.model
import android.os.Parcelable
import com.clockworkorange.repository.domain.ShouPlace
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.regex.Pattern


data class ApiShouStoreDetail(
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("store_owner")
    val storeOwner: String,
    @SerializedName("store_name")
    val storeName: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("fax")
    val fax: String,
    @SerializedName("parking")
    val parkingAddress: String,
    @SerializedName("p_lat")
    val parkingLat: Double,
    @SerializedName("p_lng")
    val parkingLng: Double,
    @SerializedName("zone_id")
    val zoneId: Int,
    @SerializedName("zone")
    val zone: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("approved")
    val approved: Int,
    @SerializedName("date_added")
    val dateAdded: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("star")
    val star: Double,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("trigger_id")
    val triggerId: String,
    @SerializedName("distance")
    val distance: Double?,
    @SerializedName("desc")
    val desc: List<StoreDesc>?
)

@Parcelize
data class StoreDesc(
    @SerializedName("description")
    val description: String,
    @SerializedName("youtube")
    val youtube: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("sort")
    val sort: Int
): Parcelable{

    fun getYoutubeId(): String?{
        if (youtube.isEmpty()) return null
        if (!youtube.contains("embed")) return null

        val p = Pattern.compile("www.youtube.com\\/embed\\/([A-Za-z0-9_\\-]{11})")
        val m = p.matcher(youtube)
        if (!m.find()) return null
        if (m.groupCount() >= 1) return m.group(1)
        return null
    }

}
