package com.clockworkorange.shou

import com.clockworkorange.repository.SHOURepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class FCMService : FirebaseMessagingService() {

    private val shouRepository: SHOURepository by lazy { (this.applicationContext as APP).appContainer.shouRepository }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        try {
            handleMessage(message)
        }catch (e: Exception){
            Timber.e(e)
        }
    }

    private fun handleMessage(message: RemoteMessage) {
        val data = message.data
        if (data.isEmpty()) return
        val title = data["title"]!!
        val msg = data["msg"]!!
        val vendorId = data["vendor_id"]!!.toInt()
        val couponId = data["coupon_id"]!!.toInt()

        shouRepository.onNewCouponReceive(title, msg, vendorId, couponId)
    }
}