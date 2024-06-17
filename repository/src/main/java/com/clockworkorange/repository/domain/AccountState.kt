package com.clockworkorange.repository.domain

import com.clockworkorange.repository.model.UserInfo

sealed class AccountState{
    object NotLogin: AccountState()
    data class LoggedIn(val data: UserInfo): AccountState()
}