package com.wordwell.libwwmw.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.wordwell.libwwmw.utils.LogUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to initialize WorkManager with the proper factory.
 * This should be called from your Application's onCreate method.
 */
@Singleton
class WorkManagerInitializer @Inject constructor(
    private val workerFactory: LibModuleWorkerFactory
) {
    /**
     * Initializes WorkManager with the custom configuration.
     * Should be called only once in the Application's onCreate.
     */
    fun initialize(context: Context) {
        try {
            LogUtils.log("Initializing WorkManager with custom configuration")
            
            val configuration = Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
                
            WorkManager.initialize(context, configuration)
            LogUtils.log("WorkManager initialized successfully")
        } catch (e: Exception) {
            LogUtils.log("Error initializing WorkManager: ${e.message}", isError = true)
        }
    }
} 