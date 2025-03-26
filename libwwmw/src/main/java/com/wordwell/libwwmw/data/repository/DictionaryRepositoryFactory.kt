package com.wordwell.libwwmw.data.repository

import android.content.Context
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.domain.audio.AudioDownloadManager
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.utils.ApiFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

// DictionaryRepositoryFactory is responsible for creating and managing instances of DictionaryRepository.
// It ensures that a single instance of the repository is used throughout the application.
object DictionaryRepositoryFactory {
    @Volatile
    private var INSTANCE: DictionaryRepository? = null

    /**
     * Provides the singleton instance of DictionaryRepository.
     * If an instance does not exist, it creates one using the provided context and API key.
     * @param context The application context
     * @param apiKey The API key for authentication
     * @param useMockApi Whether to use a mock API implementation
     * @return The singleton instance of DictionaryRepository
     */
    fun getInstance(
        context: Context,
        apiKey: String,
        useMockApi: Boolean = false
    ): DictionaryRepository {
        return INSTANCE ?: synchronized(this) {
            DictionaryRepositoryImpl(
                api = ApiFactory.createApiService(context, useMockApi),
                db = DictionaryDatabase.getInstance(context),
                context = context.applicationContext,
                apiKey = apiKey,
                audioDownloadManager = AudioDownloadManager(
                    context,
                    dictionaryDatabase = DictionaryDatabase.getInstance(context),
                    coroutineScope = CoroutineScope(Dispatchers.IO)
                )
            ).also { INSTANCE = it }
        }
    }
    
    /**
     * Clears the singleton instance of DictionaryRepository.
     * This should be called when the repository is no longer needed to prevent memory leaks.
     */
    fun clearInstance() {
        synchronized(this) {
            INSTANCE = null
        }
    }
}