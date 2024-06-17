package com.clockworkorange.shou

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatDelegate

class CarLightReceiver : BroadcastReceiver() {

    companion object{
        private const val ACTION = "com.imotor.action.LIGHT_STATE"
        private const val KEY_LIGHT_STATE = "state"
        private const val LIGHT_OFF = false
        private const val LIGHT_ON = true

        private const val KEY_NIGHT_MODE = "KEY_NIGHT_MODE"

        private var receiver: CarLightReceiver? = null

        fun register(context: Context){
            receiver = CarLightReceiver()
            context.registerReceiver(receiver, IntentFilter(ACTION))
        }

        fun unregister(context: Context){
            if (receiver != null){
                context.unregisterReceiver(receiver)
                receiver = null
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getBooleanExtra(KEY_LIGHT_STATE, LIGHT_OFF)

        if (state == LIGHT_ON) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}