package com.clockworkorange.shou.ui.tab.account

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.clockworkorange.repository.UserRepository
import com.clockworkorange.shou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AccountViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    val accountState = userRepository.getAccountState().asLiveData(genericExceptionHandler)

    fun logout() = viewModelScope.launch(genericExceptionHandler) {
        userRepository.logout()
    }



}