package com.wordwell.libwwmw.di

import android.content.Context
import com.wordwell.libwwmw.data.repository.DictionaryRepositoryFactory
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.domain.usecases.GetCachedWordsUseCase
import com.wordwell.libwwmw.domain.usecases.GetWordUseCase
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.WordDetailViewModel
import com.wordwell.libwwmw.utils.Constants
import java.lang.ref.WeakReference

/**
 * Simple dependency injection container
 */
class DictionaryContainer(
    private val context: Context,
    private val apiKey: String,
    private val useMockApi: Boolean = false
) {
    private val repository: DictionaryRepository by lazy {
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

    companion object {
        @Volatile
        private var INSTANCE: DictionaryContainer? = null

        fun getInstance(
            context: Context, 
            apiKey: String, 
            useMockApi: Boolean = false
        ): DictionaryContainer {
            Constants.initializeApiKey(context, apiKey)
            return INSTANCE ?: synchronized(this) {
                DictionaryContainer(context.applicationContext, apiKey, useMockApi).also {
                    INSTANCE = it
                }
            }
        }
        
        // Method to clear the instance when no longer needed
        fun clearInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }
    }
}