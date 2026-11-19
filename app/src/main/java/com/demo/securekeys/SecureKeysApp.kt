package com.demo.securekeys

import android.app.Application
import android.util.Log

/**
 * Application entry point: loads constants from C++ before any Activity starts.
 */
class SecureKeysApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppConstants.init()
        if (BuildConfig.DEBUG) {
            // Demo logging only — never log secrets in a real app.
            Log.d(TAG, "Constants loaded from native layer. BASE_URL=${AppConstants.BASE_URL}")
        }
    }

    private companion object {
        const val TAG = "SecureKeysApp"
    }
}
