package com.clockworkorange.shou.ui.tab.main

import android.content.Intent

data class NavigationAction(
    val command: VoiceCommand,
    val arg: Map<String, String>,
    val speakText: String
){
    companion object{
        fun fromIntent(intent: Intent): NavigationAction?{
            val command: String? = intent.data?.getQueryParameter("command") ?: return null
            val arg: String? = intent.data?.getQueryParameter("arg") ?: return null
            val speakText: String = intent.data?.getQueryParameter("speak_text") ?: return null

            if (command == null || arg == null){
                throw IllegalArgumentException("wrong arg")
            }

            val voiceCommand = VoiceCommand.fromVoiceInput(command)
            if (voiceCommand == VoiceCommand.UnKnown){
                throw java.lang.IllegalArgumentException("wrong command")
            }

            //split k1:v1,k2:v2 to []
            val argMap = hashMapOf<String, String>()
            arg.split(",")
                .forEach {
                    val kv = it.split(":")
                    if (kv.size != 2) return@forEach
                    val k = kv[0]
                    val v = kv[1]
                    argMap[k] = v
                }

            return NavigationAction(
                voiceCommand,
                argMap,
                speakText
            )


        }
    }

}

enum class VoiceCommand{
    RecommendCoupon,
    RecommendArea,
    FavoriteList,
    UnKnown;
    //TODO 新增語音搜尋的enum

    companion object{
        fun fromVoiceInput(command: String):VoiceCommand{
            //TODO 確認語音搜尋的command
            return when(command){
                "recommend_coupon" -> RecommendCoupon
                "recommend_area" -> RecommendArea
                "favorite_list" -> FavoriteList
                else -> UnKnown
            }
        }
    }
}