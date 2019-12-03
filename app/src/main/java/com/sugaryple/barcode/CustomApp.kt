package com.sugaryple.barcode

import android.app.Application
import timber.log.Timber.DebugTree
import timber.log.Timber

class CustomApp: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            // FIXME リリース前にはCrashlytics追加が必要
        }
    }
}