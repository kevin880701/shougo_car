package com.clockworkorange.shou

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import com.clockworkorange.repository.LocationObserver
import com.clockworkorange.repository.SHOURepository
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.repository.model.MqttShouData
import com.clockworkorange.repository.remote.mqtt.MqttConfig
import com.clockworkorange.shou.util.NotificationHelper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber


/**
 * start locationObserver and upload location
 */
@FlowPreview
@ExperimentalCoroutinesApi
class ShouGoService : LifecycleService() {

    companion object {
        private const val CLASS_NAME = ".ShouGoService"
        private const val ForegroundNotificationId = 775

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        var isNetWork = false
        var isGps = false
        var isGpsSignal = false

        fun start(context: Context) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // 循環檢查，直到同時滿足 網路 和 GPS 啟用條件
            CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    // 判斷GPS是否開啟
                    isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val network = connectivityManager.activeNetwork
                    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                            isNetWork = true
                        } else {
                            isNetWork = true
                        }
                    } else {
                        isNetWork = false
                    }
                    // 提示訊息
                    var message = StringBuilder()
                    if (!isNetWork) {
                        message.append("網路未開啟")
                    }
                    if (!isGps) {
                        if (message.isNotEmpty()) {
                            message.append("、")
                        }
                        message.append("GPS未開啟")
                    }
                    // 顯示錯誤 Toast 提示
                    if (message.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                // Android 10及以上的处理逻辑
//                                Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
//                            } else {
                            // Android 10以下的处理逻辑
                            val inflater = LayoutInflater.from(context)
                            val layout = inflater.inflate(R.layout.toast_check, null)

                            val toast = Toast(context)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.duration = Toast.LENGTH_LONG
                            toast.view = layout
                            layout.findViewById<TextView>(R.id.textToast).text = message.toString()
                            toast.show()
//                            }
                        }
                    } else {
                        // 網路 和 GPS 都已啟用，執行功能
                        if (isServiceRunning(context)) return@launch
                        val intent = Intent(context, ShouGoService::class.java).apply {
                            action = ACTION_START
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
//                        break // 退出循環

                    }
                    // 等待一段時間後再次檢查，以避免頻繁檢查
//                Thread.sleep(5000)
                    delay(4000)
                }
            }
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 在這裡處理新位置的信息
                val latitude = location.latitude
                val longitude = location.longitude
                Timber.d(latitude.toString() + "@" + longitude)
                isGpsSignal = true
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        fun createStopIntent(context: Context) = Intent(context, ShouGoService::class.java).apply {
            action = ACTION_STOP
        }

        private fun isServiceRunning(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val isRunning =
                am.getRunningServices(5).find { it.service.shortClassName == CLASS_NAME } != null
            Timber.d("Is ShouGoService running? $isRunning")
            return isRunning
        }
    }

    private lateinit var mqttUploadClient: MqttAndroidClient
    private lateinit var mqttReceiveClient: MqttAndroidClient

    private val userRepository: UserRepository by lazy { (applicationContext as APP).appContainer.userRepository }
    private val shouRepository: SHOURepository by lazy { (applicationContext as APP).appContainer.shouRepository }
    private val locationObserver: LocationObserver by lazy { (applicationContext as APP).appContainer.locationObserver }
    private val gson: Gson by lazy { (applicationContext as APP).appContainer.gson }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent.action) {
            ACTION_START -> {
                locationObserver.start()
                initUploadClient()
                initReceiveClient()
                startObserveLocationChange()
                foregroundService()

            }

            ACTION_STOP -> {
                try {
                    locationObserver.stop()
                    mqttUploadClient.disconnect()
                    mqttUploadClient.close()

                    mqttReceiveClient.disconnect()
                    mqttReceiveClient.close()
                } catch (e: Exception) {
                    //ignore
                }
                stopForeground(true)
                stopSelf()
            }

        }

        return START_REDELIVER_INTENT
    }

    private fun createMqttConnectOptions(deviceId: String) = MqttConnectOptions().apply {
        isCleanSession = true
        connectionTimeout = 10
        keepAliveInterval = 20
        isAutomaticReconnect = true
        val message = "{\"client_id\":\"$deviceId\",\"error\":\"disconnected\"}"
        setWill(MqttConfig.createUploadTopic(deviceId), message.toByteArray(), 2, false)
    }

    private fun initUploadClient() {
        mqttUploadClient = MqttAndroidClient(
            applicationContext,
            MqttConfig.UploadHost,
            MqttClient.generateClientId()
        )

        val deviceId = userRepository.getDeviceId()
        mqttUploadClient.connect(
            createMqttConnectOptions(deviceId),
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Timber.d("mqtt upload client connect success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.d("mqtt upload client connect fail")
                    exception?.let { Timber.e(exception) }
                    exception?.printStackTrace()
                }
            })
    }

    private fun initReceiveClient() {
        mqttReceiveClient = MqttAndroidClient(
            applicationContext,
            MqttConfig.ReceiveHost,
            MqttClient.generateClientId()
        )

        val deviceId = userRepository.getDeviceId()

        mqttReceiveClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Timber.d("mqttReceiveClient connectComplete reconnect:$reconnect")

                val subscribeTopic = MqttConfig.createReceiveTopic(deviceId)
                Timber.d("subscribeTopic: $subscribeTopic")
                mqttReceiveClient.subscribe(subscribeTopic, 0, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Timber.d("subscribe success")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.d("subscribe fail")
                    }
                })

            }

            override fun connectionLost(cause: Throwable?) {
                Timber.d("connectionLost ${cause?.localizedMessage}")
                Timber.e(cause)
                cause?.printStackTrace()
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                message ?: return
                Timber.d("messageArrived topic:$topic, message: ${String(message.payload)}")
                try {
                    val json = String(message.payload)
                    val data = gson.fromJson(json, MqttShouData::class.java)
                    shouRepository.onReceiveNearData(data)
                } catch (e: Exception) {
                    Timber.e(e, "messageArrived exception")
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        mqttReceiveClient.connect(
            createMqttConnectOptions(deviceId),
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Timber.d("mqttReceiveClient connect success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.d("mqttReceiveClient connect fail")
                    exception?.printStackTrace()
                }
            })

    }

    private val genericExceptionHandler = CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }

    private fun startObserveLocationChange() {
        // 使用 LifecycleService 的原因
        locationObserver.locationFlow.asLiveData(genericExceptionHandler).observe(this) {
            Timber.d("locationFlow $it")
            uploadLocation(it)
        }
    }

    private fun uploadLocation(location: Location) {
        if (::mqttUploadClient.isInitialized && mqttUploadClient.isConnected) {
            Timber.d("uploadLocation $location")

            val message = MqttConfig.createUploadPayload(
                userRepository.getDeviceId(),
                location.latitude,
                location.longitude
            )

            mqttUploadClient.publish(
                MqttConfig.createUploadTopic(userRepository.getDeviceId()),
                message,
                0,
                false,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Timber.d("publish onSuccess deviceId:${userRepository.getDeviceId()} lat: ${location.latitude}, lng: ${location.longitude}")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.d("publish onFailure")
                    }
                })
        }
    }

    private fun foregroundService() {
        val notification =
            NotificationHelper.createNotification(applicationContext, "ShouGo", "背景執行中")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                ForegroundNotificationId,
                notification,
                FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(ForegroundNotificationId, notification)
        }
    }


}
