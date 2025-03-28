package com.wordwell.libwwmw.di

import android.content.Context
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.domain.audio.AudioDownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Dagger module for providing audio-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    
    /**
     * Provides an application-scoped CoroutineScope for background operations.
     */
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    
    /**
     * Provides the AudioDownloadManager.
     * WorkManager is injected from WorkManagerModule.
     */
    @Provides
    @Singleton
    fun provideAudioDownloadManager(
        @ApplicationContext context: Context,
        dictionaryDatabase: DictionaryDatabase,
        coroutineScope: CoroutineScope
    ): AudioDownloadManager {
        return AudioDownloadManager(context, dictionaryDatabase, coroutineScope)
    }
} 