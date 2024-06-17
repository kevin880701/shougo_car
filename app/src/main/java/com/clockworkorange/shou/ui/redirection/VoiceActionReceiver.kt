package com.clockworkorange.shou.ui.redirection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import timber.log.Timber

class VoiceActionReceiver(private val listener: Listener): BroadcastReceiver(){

    interface Listener{
        fun onSelectItem(index: Int)
        fun onNextPage()
        fun onPreviousPage()
    }

    companion object{
        private const val ACTION_VOICE_INPUT = "action_voice_input"

        private const val KEY_SELECT_ITEM = "select_item"
        private const val KEY_PAGE_SWITCH = "voice_control"
        private const val NEXT_PAGE = "NEXT_PAGE"
        private const val PRE_PAGE = "PRE_PAGE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("""
            intent 
                action ${intent.action}
                第 n 個 ${intent.getIntExtra(KEY_SELECT_ITEM, -1)}
                切換頁 ${intent.getStringExtra(KEY_PAGE_SWITCH)}
        """.trimIndent())

        val selectItemIndex = intent.getIntExtra(KEY_SELECT_ITEM, -1)
        val switchPage = intent.getStringExtra(KEY_PAGE_SWITCH)
        if (selectItemIndex != -1){
            listener.onSelectItem(selectItemIndex)
        } else if (switchPage == NEXT_PAGE) {
            listener.onNextPage()
        } else if (switchPage == PRE_PAGE) {
            listener.onPreviousPage()
        }
    }

    fun register(context: Context){
        Timber.d("register")
        val filter = IntentFilter(ACTION_VOICE_INPUT)
        context.registerReceiver(this, filter)
    }

    fun unRegister(context: Context){
        Timber.d("unRegister")
        context.unregisterReceiver(this)
    }
}