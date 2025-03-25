package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.usecases.FetchCachedWordsUseCase
import com.wordwell.libwwmw.domain.usecases.FetchWordsBySetUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CachedWordsViewModel manages the UI-related data for displaying cached words.
 * It interacts with the use case to load cached words and update the UI state.
 */
class CachedWordsViewModel @Inject constructor(
    private val getCachedWordsUseCase: FetchCachedWordsUseCase,
    private val fetchWordsByStrategyUseCase: FetchWordsBySetUseCase
) : BaseViewModel<List<Word>>() {

    init {
       // loadCachedWords()
    }

    /**
     * Loads the cached words using the use case.
     * Updates the UI state based on the retrieved words.
     */
    private fun loadCachedWords() {
        viewModelScope.launch {
            setLoading()
            getCachedWordsUseCase().collect { result ->
                when (result) {
                    is DictionaryFetchResult.Success -> setSuccess(result.data)
                    else -> setError("Failed to load cached words")
                }
            }
        }
    }

    fun fetchWordsBySetName(setName: String, words: List<String> = emptyList()) {
        viewModelScope.launch {
            setLoading()
            if (words.isEmpty()) {
                fetchWordsByStrategyUseCase(setName).collect { result ->
                    when (result) {
                        is DictionaryFetchResult.Success -> {
                            setSuccess(result.data)
                        }
                        else -> setError("Failed to load cached words")
                    }
                }
            } else {
                fetchWordsByStrategyUseCase(setName, words).collect { result ->
                    when (result) {
                        is DictionaryFetchResult.Success -> {
                            setSuccess(result.data)
                        }
                        else -> setError("Failed to load cached words")
                    }
                }
            }
        }
    }

    /**
     * Factory for creating instances of CachedWordsViewModel.
     * Provides the necessary use case dependency.
     */
    class Factory(
        private val getCachedWordsUseCase: FetchCachedWordsUseCase,
        private val fetchWordsByStrategyUseCase: FetchWordsBySetUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CachedWordsViewModel::class.java)) {
                return CachedWordsViewModel(getCachedWordsUseCase, fetchWordsByStrategyUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 