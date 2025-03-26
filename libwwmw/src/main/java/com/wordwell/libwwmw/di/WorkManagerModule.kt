package com.wordwell.libwwmw.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Module for configuring WorkManager with custom worker factories.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    /**
     * Provides a singleton WorkManager instance for parts of the app
     * that need it after initialization.
     * 
     * Note: WorkManager.initialize() is handled by WorkManagerInitializer.
     */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}

/**
 * Custom WorkerFactory that handles creating workers with dependency injection.
 */
class LibModuleWorkerFactory @Inject constructor(
    private val workerProviders: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ListenableWorker>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = try {
            Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        } catch (e: ClassNotFoundException) {
            return null
        }
        return workerProviders[workerClass]?.get()
    }
} 