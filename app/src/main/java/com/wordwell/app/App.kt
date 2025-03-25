package com.wordwell.app

import android.app.Application
import androidx.work.Configuration
import com.wordwell.libwwmw.di.LibModuleWorkerFactory
import com.wordwell.libwwmw.di.WorkManagerInitializer
import timber.log.Timber
import javax.inject.Inject

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}