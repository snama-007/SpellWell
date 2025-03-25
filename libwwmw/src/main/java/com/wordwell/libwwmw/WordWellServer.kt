package com.wordwell.libwwmw

import android.content.Context
import com.wordwell.libwwmw.data.repository.DictionaryRepositoryFactory
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.domain.usecases.FetchCachedWordsUseCase
import com.wordwell.libwwmw.domain.usecases.FetchWordsBySetUseCase
import com.wordwell.libwwmw.domain.usecases.GetWordUseCase
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.WordDetailViewModel
import com.wordwell.libwwmw.utils.Constants
import java.lang.ref.WeakReference
import javax.inject.Inject

// DictionaryContainer is a simple dependency injection container for managing dependencies
// related to dictionary operations, including repositories and use cases.
class WordWellServer @Inject constructor(
    applicationContext: Context,
    private val apiKey: String,
    private val useMockApi: Boolean = false
) {
    // Store application context as a weak reference to prevent memory leaks
    private val contextRef = WeakReference(applicationContext)

    private val repository: DictionaryRepository by lazy {
        val context = contextRef.get() ?: throw IllegalStateException("Context is no longer available")
        DictionaryRepositoryFactory.getInstance(context, apiKey, useMockApi)
    }

    private val getWordUseCase: GetWordUseCase by lazy {
        GetWordUseCase(repository)
    }

    private val getCachedWordsUseCase: FetchCachedWordsUseCase by lazy {
        FetchCachedWordsUseCase(repository)
    }

    private val getWordsBySetUseCase: FetchWordsBySetUseCase by lazy {
        FetchWordsBySetUseCase(repository)
    }

    val wordDetailViewModelFactory: WordDetailViewModel.Factory by lazy {
        WordDetailViewModel.Factory(
            getWordUseCase,
        )
    }

    val cachedWordsViewModelFactory: CachedWordsViewModel.Factory by lazy {
        CachedWordsViewModel.Factory(
            getCachedWordsUseCase,
            fetchWordsByStrategyUseCase = getWordsBySetUseCase,
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: WordWellServer? = null

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
        ): WordWellServer {
            // Always use application context to prevent memory leaks
            val applicationContext = context.applicationContext

            // Initialize API key
            Constants.initializeApiKey(applicationContext, apiKey)
            
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WordWellServer(applicationContext, apiKey, useMockApi).also {
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