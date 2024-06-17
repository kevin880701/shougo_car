package com.clockworkorange.shou.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import com.clockworkorange.repository.domain.TravelWayPoint
import timber.log.Timber


object RouteUtil {

    //https://developers.google.com/maps/documentation/urls/get-started#directions-action
    fun startRoute(context: Context, currentLocation: Location, places: List<TravelWayPoint>){
        val firstWaypoint = places.first()
        val lastWaypoint = places.last()

        val googleMapUrl = buildString {
            append("http://www.google.com/maps/dir/?api=1")
            append("&origin=${currentLocation.latitude},${currentLocation.longitude}")
            append("&destination=${createDestQueryString(lastWaypoint)}")
            append("&travelmode=driving")

            if (places.size > 1){
                append("&waypoints=${firstWaypoint.lat},${firstWaypoint.lng}")

                for (i in 1..places.size-2){
                    append("|${places[i].lat},${places[i].lng}")
                }
            }
        }

        val uri = Uri.parse(googleMapUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private fun createDestQueryString(place: TravelWayPoint): String{
        return if (place.isNavParking && place.hasParking()){
            if (place.parkingAddress.isNotEmpty()){
                place.parkingAddress
            }else{
                "${place.parkingLat},${place.parkingLng}"
            }
        }else{
            "${place.lat},${place.lng}"
        }
    }

    fun startRoute(context: Activity, currentLocation: Location, destLat: Double, destLng: Double){
//        val googleMapUrl = buildString {
//            append("http://www.google.com/maps/dir/?api=1")
//            append("&origin=${currentLocation.latitude},${currentLocation.longitude}")
//            append("&destination=$destLat,$destLng")
//            append("&travelmode=driving")
//        }
//
//        val uri = Uri.parse(googleMapUrl)
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        context.startActivity(intent)

        naviTo(context, "", destLat, destLng)

    }

    private fun isAppAlive(context: Context, packageName: String):Boolean{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.runningAppProcesses.forEach {
            if (it.processName.equals(packageName)){
                return true
            }
        }
        return false
    }

    private fun appIsExist(context: Activity, packageNameTarget: String): Boolean{
        val packageManager: PackageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(packageNameTarget, 0)
            true
        }catch (e: PackageManager.NameNotFoundException){
            false
        }
    }

    private fun startLocalApp(context: Activity, packageNameTarget: String, isStartingFromBackgroundActivity: Boolean): Boolean{
        var bApp = false

        if (appIsExist(context, packageNameTarget)) {
            val packageManager: PackageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageNameTarget)
            intent?.runCatching {
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK
                action = "android.intent.action.MAIN"
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(this)
            }

            bApp = true
        } else {
            Toast.makeText(context.applicationContext, "PAPAGO未安裝", Toast.LENGTH_LONG).show()
        }
        return bApp
    }

    private fun naviTo(activity: Activity, name: String, destLat: Double, destLng: Double){
        val strPackageName = "com.papago.s1OBU"

        var bAlive = false
        bAlive = isAppAlive(activity, strPackageName) // 先檢查S2是否已經在背景中執行


        if (startLocalApp(activity, strPackageName, false)) { // 1.在背景中執行:叫S2到最上面    2.不在背景中執行:開始執行S2
            if (!bAlive) { // 不在背景中執行:開始執行S2需要等3秒
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            val intent = Intent()
            intent.action = "PAPAGO_BROADCAST_RECV"
            intent.putExtra("KEY_TYPE", 10038)
            intent.putExtra("POINAME", name) // 目的地名稱(店名或地址皆可)
            intent.putExtra("LAT", destLat) // 座標:緯度
            intent.putExtra("LON", destLng) // 座標:經度
            intent.putExtra("DEV", 0) // 無作用
            intent.putExtra("STYLE", 0) // 無作用
            intent.putExtra("SOURCE_APP", "SHOU GO") // 你的APP名稱
            activity.sendBroadcast(intent)
        }
    }

}