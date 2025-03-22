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
 * ViewModel for displaying cached words list
 */
class CachedWordsViewModel(
    private val getCachedWordsUseCase: GetCachedWordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CachedWordsUiState>(CachedWordsUiState.Loading)
    val uiState: StateFlow<CachedWordsUiState> = _uiState.asStateFlow()

    init {
        loadCachedWords()
    }

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

    sealed class CachedWordsUiState {
        data object Loading : CachedWordsUiState()
        data object Empty : CachedWordsUiState()
        data class Success(val words: List<Word>) : CachedWordsUiState()
    }

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