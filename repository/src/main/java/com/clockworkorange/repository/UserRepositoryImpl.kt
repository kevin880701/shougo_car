package com.clockworkorange.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.clockworkorange.repository.domain.AccountState
import com.clockworkorange.repository.remote.http.ShouServiceWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.resume

@ExperimentalCoroutinesApi
class UserRepositoryImpl(private val sharedPreferences: SharedPreferences, private val shouService: ShouServiceWrapper): UserRepository {

    companion object {
        private const val KEY_READ_PRIVACY = "KEY_READ_PRIVACY"
    }

    private val cacheAccountState = MutableStateFlow<AccountState>(AccountState.NotLogin)

    override fun getDeviceId(): String {
        return shouService.getDeviceId()
    }

    override fun getUserEmail(): String {
        return if (cacheAccountState.value is AccountState.LoggedIn){
            (cacheAccountState.value as AccountState.LoggedIn).data.email
        }else{
            ""
        }
    }

    override suspend fun isAlreadyReadPrivacy(): Boolean = suspendCancellableCoroutine {
        it.resume(sharedPreferences.getBoolean(KEY_READ_PRIVACY, false))
    }

    override fun setAlreadyReadPrivacy() {
        sharedPreferences.edit{
            putBoolean(KEY_READ_PRIVACY, true)
        }
    }

    override suspend fun isCarAppLogin(): Boolean {
        if (cacheAccountState.value != AccountState.NotLogin) return true

        try {
            val isLogin = shouService.isCarAppLogin()
            if (isLogin) {
                val userInfo = shouService.getUserInfo()
                cacheAccountState.value = AccountState.LoggedIn(userInfo)
                return true
            }
            return false
        }catch (e: ConnectException){
            throw e
        }catch (e: UnknownHostException) {
            throw e
        }catch (e: SocketTimeoutException){
            throw e
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun logout() {
        if (shouService.logout()) {
            cacheAccountState.value = AccountState.NotLogin
        }
    }

    override fun getAccountState(): StateFlow<AccountState> {
        return cacheAccountState
    }

    override fun isAppLogin(): Boolean {
        return cacheAccountState.value != AccountState.NotLogin
    }

    override suspend fun uploadFCMToken(token: String): Boolean {
        return shouService.uploadFCMToken(token)
    }
}