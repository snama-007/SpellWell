package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.usecases.GetCachedWordsUseCase
import kotlinx.coroutines.launch

/**
 * CachedWordsViewModel manages the UI-related data for displaying cached words.
 * It interacts with the use case to load cached words and update the UI state.
 */
class CachedWordsViewModel(
    private val getCachedWordsUseCase: GetCachedWordsUseCase
) : BaseViewModel<List<Word>>() {

    init {
        loadCachedWords()
    }

    /**
     * Loads the cached words using the use case.
     * Updates the UI state based on the retrieved words.
     */
    fun loadCachedWords() {
        viewModelScope.launch {
            setLoading()
            getCachedWordsUseCase().collect { words ->
                if (words.isEmpty()) {
                    setError("No cached words found")
                } else {
                    setSuccess(words)
                }
            }
        }
    }

    /**
     * Factory for creating instances of CachedWordsViewModel.
     * Provides the necessary use case dependency.
     */
    class Factory(private val getCachedWordsUseCase: GetCachedWordsUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CachedWordsViewModel::class.java)) {
                return CachedWordsViewModel(getCachedWordsUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 