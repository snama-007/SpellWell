package com.wordwell.libwwmw.di

import android.content.Context
import com.wordwell.libwwmw.data.repository.DictionaryRepositoryFactory
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.domain.usecases.GetCachedWordsUseCase
import com.wordwell.libwwmw.domain.usecases.GetWordUseCase
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.WordDetailViewModel
import com.wordwell.libwwmw.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

// DictionaryContainer is a simple dependency injection container for managing dependencies
// related to dictionary operations, including repositories and use cases.
class DictionaryContainer private constructor(
    applicationContext: Context,
    private val apiKey: String,
    private val useMockApi: Boolean = false
) {
    private val coroutineManager = CoroutineManager()

    // Store application context as a weak reference to prevent memory leaks
    private val contextRef = WeakReference(applicationContext)
    
    private val repository: DictionaryRepository by lazy {
        val context = contextRef.get() ?: throw IllegalStateException("Context is no longer available")
        DictionaryRepositoryFactory.getInstance(context, apiKey, useMockApi)
    }

    private val getWordUseCase: GetWordUseCase by lazy {
        GetWordUseCase(repository)
    }

    private val getCachedWordsUseCase: GetCachedWordsUseCase by lazy {
        GetCachedWordsUseCase(repository)
    }

    val wordDetailViewModelFactory: WordDetailViewModel.Factory by lazy {
        WordDetailViewModel.Factory(getWordUseCase)
    }

    val cachedWordsViewModelFactory: CachedWordsViewModel.Factory by lazy {
        CachedWordsViewModel.Factory(getCachedWordsUseCase)
    }

    /**
     * Performs a background operation using the coroutine manager.
     * This method demonstrates how to launch coroutines for background tasks.
     */
    fun performBackgroundOperation() {
        coroutineManager.scope.launch {
            // Perform background operations here
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DictionaryContainer? = null

        /**
         * Gets the singleton instance of DictionaryContainer
         * 
         * @param context The application context
         * @param apiKey The Merriam-Webster API key
         * @param useMockApi Whether to use mock API implementation
         * @return The singleton instance of DictionaryContainer
         */
        fun getInstance(
            context: Context, 
            apiKey: String, 
            useMockApi: Boolean = false
        ): DictionaryContainer {
            // Always use application context to prevent memory leaks
            val applicationContext = context.applicationContext
            
            // Initialize API key
            Constants.initializeApiKey(applicationContext, apiKey)
            
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DictionaryContainer(applicationContext, apiKey, useMockApi).also {
                    INSTANCE = it
                }
            }
        }
        
        /**
         * Clears the singleton instance
         * Should be called when the app is being terminated or when the container is no longer needed
         */
        fun clearInstance() {
            synchronized(this) {
                // Also clear repository instance to prevent memory leaks
                INSTANCE?.let {
                    DictionaryRepositoryFactory.clearInstance()
                }
                INSTANCE = null
            }
        }
    }
}

class CoroutineManager {
    private val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    fun clear() {
        scope.cancel()
    }
}