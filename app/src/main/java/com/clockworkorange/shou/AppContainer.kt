package com.clockworkorange.shou

import android.content.Context
import androidx.preference.PreferenceManager
import com.clockworkorange.repository.*
import com.clockworkorange.repository.remote.http.ShouService
import com.clockworkorange.repository.remote.http.ShouServiceWrapper
import com.google.gson.Gson
import kotlinx.coroutines.*
import timber.log.Timber

@ExperimentalCoroutinesApi
class AppContainer(private val context: Context) {

    val gson by lazy { Gson() }

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, e ->
        Timber.e(e, "appCoroutineScope coroutineExceptionHandler")
    }

    private val appCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("ApplicationScope") + coroutineExceptionHandler)

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private val shouService by lazy { ShouServiceWrapper(ShouService.create(), sharedPreferences, gson) }

    val userRepository: UserRepository by lazy { UserRepositoryImpl(sharedPreferences, shouService) }

    val shouRepository: SHOURepository by lazy { SHOURepositoryImpl(shouService, userRepository, appCoroutineScope, Dispatchers.IO) }

    private val gpsLocationObserver: LocationObserver by lazy { LocationObserverImpl(context, appCoroutineScope, Dispatchers.IO) }

    private val fakeLocationObserver: LocationObserver by lazy { FakeLocationObserver() }

    val locationObserver: LocationObserver by lazy { gpsLocationObserver }

    val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            context,
            userRepository,
            shouRepository,
            locationObserver
        )
    }
}
