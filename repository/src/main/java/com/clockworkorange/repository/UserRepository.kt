package com.clockworkorange.repository

import com.clockworkorange.repository.domain.AccountState
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {

    fun getDeviceId(): String

    fun getUserEmail(): String

    suspend fun isAlreadyReadPrivacy(): Boolean

    fun setAlreadyReadPrivacy()

    suspend fun isCarAppLogin(): Boolean

    suspend fun logout()

    fun getAccountState(): StateFlow<AccountState>

    fun isAppLogin(): Boolean

    suspend fun uploadFCMToken(token: String): Boolean
}