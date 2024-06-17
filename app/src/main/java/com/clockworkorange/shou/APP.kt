package com.clockworkorange.shou

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.clockworkorange.shou.ui.tab.main.NavigationAction
import com.clockworkorange.shou.util.CrashlyticsTree
import timber.log.Timber
import timber.log.Timber.DebugTree

class APP: Application() {

    val appContainer = AppContainer(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(DebugTree())
        }else{
            Timber.plant(CrashlyticsTree())
        }

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.e(e)
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    companion object{
        var navAction: NavigationAction? = null
        val nightMode = MutableLiveData(false)
    }

}