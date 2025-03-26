package com.wordwell.demo

import android.app.Application
import timber.log.Timber

class DemoApp : Application(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}