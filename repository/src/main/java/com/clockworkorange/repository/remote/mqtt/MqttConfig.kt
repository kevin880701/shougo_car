package com.clockworkorange.repository.remote.mqtt

import android.util.Log


object MqttConfig {

    const val UploadHost = "tcp://35.201.197.226:1883"
    fun createUploadTopic(deviceId: String): String = "com/cwo/car/${deviceId}"

    fun createUploadPayload(deviceId: String, lat: Double, lng: Double): ByteArray{
        val data = "{\"device_id\":\"$deviceId\",\"lat\":\"$lat\",\"lng\":\"${lng}\"}"
//        Log.d("Mqtt", "push $data")
        return data.toByteArray()
    }

    const val ReceiveHost = "tcp://35.201.197.37:1883"
    fun createReceiveTopic(deviceId: String): String = "com/cwo/trigger/car/${deviceId}"


}