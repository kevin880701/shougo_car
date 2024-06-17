package com.clockworkorange.shou.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clockworkorange.shou.ShouGoService
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED != intent.action) return
        Timber.d("ACTION_BOOT_COMPLETED")
        ShouGoService.start(context.applicationContext)
    }
}