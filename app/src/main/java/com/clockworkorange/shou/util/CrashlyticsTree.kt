package com.clockworkorange.shou.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import timber.log.Timber

class CrashlyticsTree: Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        FirebaseCrashlytics.getInstance().setCustomKeys {
            key(CRASHLYTICS_KEY_PRIORITY, priority)
            key(CRASHLYTICS_KEY_TAG, tag ?: "")
            key(CRASHLYTICS_KEY_MESSAGE, message)
        }

        if (t == null) {
            FirebaseCrashlytics.getInstance().recordException(Exception(message))
        } else {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }

    companion object {
        private val CRASHLYTICS_KEY_PRIORITY = "priority"
        private val CRASHLYTICS_KEY_TAG = "tag"
        private val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}