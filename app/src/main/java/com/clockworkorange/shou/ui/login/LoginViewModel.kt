package com.clockworkorange.shou.ui.login

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.shou.APP
import com.clockworkorange.shou.util.SingleLiveEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel(context: Context, private val userRepository: UserRepository): AndroidViewModel(context.applicationContext as APP) {

    private val genericExceptionHandler = CoroutineExceptionHandler{ _, e ->
        Timber.e(e)
    }

    val showRequireLocationPermission = SingleLiveEvent<Any>()
    val showPrivacy = SingleLiveEvent<Any>()
    val navigateMainScreen = SingleLiveEvent<Any>()

    val loginQRCodeContent = MutableLiveData<String>()

    val eventStartShouService = SingleLiveEvent<Void>()

    private val _eventToast = SingleLiveEvent<String>()
    val eventToast: LiveData<String> get() = _eventToast

    val skipLoginEnable = MutableLiveData<Boolean>(true)

    val eventNetError = SingleLiveEvent<Unit>()

    init {
        checkLocationPermission()
        loginQRCodeContent.value = userRepository.getDeviceId()
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplication(),android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            showRequireLocationPermission.call()
        }else{
            checkReadPrivacy()
            eventStartShouService.call()
        }
    }

    fun setLocationPermissionGranted(){
        checkReadPrivacy()
        eventStartShouService.call()
    }

    private fun checkReadPrivacy() = viewModelScope.launch(genericExceptionHandler) {
        if (!userRepository.isAlreadyReadPrivacy()){
            showPrivacy.call()
        }else{
            checkLoginStatus()
        }
    }

    fun setPrivacyRead() {
        userRepository.setAlreadyReadPrivacy()
        checkLoginStatus()
    }

    private fun checkLoginStatus() = viewModelScope.launch{
        while (true){
            try {
                val isLogin = userRepository.isCarAppLogin()
                if (isLogin){
                    FirebaseCrashlytics.getInstance().setUserId(userRepository.getUserEmail())
                    //已登入
                    navigateMainScreen.call()
                    break
                }else{
                    //如果沒登入
                    //3秒輪詢一次api
                    delay(3000L)
                }
                skipLoginEnable.postValue(true)
            }catch (e: ConnectException){
                //no network
                _eventToast.value = "查無網路，請檢查網路狀態"
                eventNetError.call()
                skipLoginEnable.postValue(false)
                break
            }catch (e: UnknownHostException){
                _eventToast.value = "查無網路，請檢查網路狀態"
                eventNetError.call()
                skipLoginEnable.postValue(false)
                break
            }catch (e: SocketTimeoutException){
                _eventToast.value = "查無網路，請檢查網路狀態"
                eventNetError.call()
                skipLoginEnable.postValue(false)
                break
            }catch (e: Exception){
                Timber.e(e)
                _eventToast.value = e.message ?: "unknown error"
                eventNetError.call()
                skipLoginEnable.postValue(false)
                break
            }
        }

    }

}