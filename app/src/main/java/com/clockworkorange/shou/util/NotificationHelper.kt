package com.clockworkorange.shou.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.clockworkorange.shou.R
import com.clockworkorange.shou.ShouGoService

object NotificationHelper {

    private const val ChannelId = "shou_go"
    private const val ChannelName = "SHOU GO"

    fun createNotification(context: Context, title: String, content: String): Notification {
        val manager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannelCompat.Builder(ChannelId, NotificationManager.IMPORTANCE_DEFAULT)
                    .setName(ChannelName)
                    .setDescription("GPS")
                    .build()

            manager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getService(context, 1001, ShouGoService.createStopIntent(context), PendingIntent.FLAG_MUTABLE)
        val action = NotificationCompat.Action(R.drawable.ic_logo, "停止", pendingIntent)

        return NotificationCompat.Builder(context, ChannelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_logo)
            .addAction(action)
            .build()
    }

}