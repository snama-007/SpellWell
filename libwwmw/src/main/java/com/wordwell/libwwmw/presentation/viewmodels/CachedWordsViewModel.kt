package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.usecases.GetCachedWordsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * CachedWordsViewModel manages the UI-related data for displaying cached words.
 * It interacts with the use case to load cached words and update the UI state.
 */
class CachedWordsViewModel(
    private val getCachedWordsUseCase: GetCachedWordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CachedWordsUiState>(CachedWordsUiState.Loading) // Holds the UI state for cached words
    val uiState: StateFlow<CachedWordsUiState> = _uiState.asStateFlow() // Exposes the UI state as a read-only StateFlow

    init {
        loadCachedWords() // Load cached words on initialization
    }

    /**
     * Loads the cached words using the use case.
     * Updates the UI state based on the retrieved words.
     */
    fun loadCachedWords() {
        viewModelScope.launch {
            getCachedWordsUseCase().collect { words ->
                _uiState.value = if (words.isEmpty()) {
                    CachedWordsUiState.Empty
                } else {
                    CachedWordsUiState.Success(words)
                }
            }
        }
    }

    /**
     * Represents the different UI states for displaying cached words.
     */
    sealed class CachedWordsUiState {
        data object Loading : CachedWordsUiState() // Represents a loading state
        data object Empty : CachedWordsUiState() // Represents an empty state when no words are cached
        data class Success(val words: List<Word>) : CachedWordsUiState() // Represents a successful state with cached words
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