package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.DictionaryResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.usecases.GetWordUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

/**
 * ViewModel for displaying word details
 */
class WordDetailViewModel(
    private val getWordUseCase: GetWordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordDetailUiState>(WordDetailUiState.Initial)
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    /**
     * Initiates a word lookup operation.
     * Fetches the word details using the use case and updates the UI state.
     * @param word The word to look up
     */
    fun lookupWord(word: String) {
        viewModelScope.launch {
            _uiState.value = WordDetailUiState.Loading
            getWordUseCase(word).collect { result ->
                _uiState.value = when (result) {
                    is DictionaryResult.Success -> WordDetailUiState.Success(result.data)
                    is DictionaryResult.Error -> WordDetailUiState.Error(result.message)
                    is DictionaryResult.Loading -> WordDetailUiState.Loading
                }
            }
        }
    }

    /**
     * Represents the different UI states for displaying word details.
     */
    sealed class WordDetailUiState {
        data object Initial : WordDetailUiState()
        data object Loading : WordDetailUiState()
        data class Success(val word: Word) : WordDetailUiState()
        data class Error(val message: String) : WordDetailUiState()
    }

    /**
     * Factory for creating instances of WordDetailViewModel.
     * Provides the necessary use case dependency.
     */
    class Factory(private val getWordUseCase: GetWordUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordDetailViewModel::class.java)) {
                return WordDetailViewModel(getWordUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 