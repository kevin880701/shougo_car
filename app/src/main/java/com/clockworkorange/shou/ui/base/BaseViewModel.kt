package com.clockworkorange.shou.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clockworkorange.shou.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import retrofit2.HttpException
import timber.log.Timber

open class BaseViewModel: ViewModel() {

    private val _eventToast = SingleLiveEvent<String>()
    val eventToast: LiveData<String> get() = _eventToast

    private val _eventMessage = SingleLiveEvent<String>()
    val eventMessage: LiveData<String> get() = _eventMessage

    private val _showLoading = MutableLiveData(false)
    val showLoading: LiveData<Boolean> get() = _showLoading

    protected val genericExceptionHandler = CoroutineExceptionHandler{ _, e ->
        if (e is HttpException){
            val response = e.response()
            val url = response?.raw()?.request?.url?.toUrl()?.toString()
            val errMsg = response?.errorBody()?.string()
            Timber.e(e, "url: $url, error: $errMsg")
        }else{
            Timber.e(e)
        }
        toast(e.localizedMessage)
        showLoading(false)
    }

    protected fun toast(msg: String){
        _eventToast.value = msg
    }

    protected fun showMessage(msg: String){
        _eventMessage.value = msg
    }

    protected fun showLoading(isShow: Boolean){
        _showLoading.value = isShow
    }
}