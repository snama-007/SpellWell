package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.usecases.GetWordUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for displaying word details
 */
class WordDetailViewModel(
    private val getWordUseCase: GetWordUseCase,
) : BaseViewModel<Word>() {

    /**
     * Initiates a word lookup operation.
     * Fetches the word details using the use case and updates the UI state.
     * @param word The word to look up
     */
    fun lookupWord(word: String) {
        viewModelScope.launch {
            setLoading()

                getWordUseCase(word).collect { result ->
                    when (result) {
                        is DictionaryFetchResult.Success -> setSuccess(result.data)
                        is DictionaryFetchResult.Error -> setError(result.message)
                        is DictionaryFetchResult.Loading -> setLoading()
                    }
                }
            }
    }

    /**
     * Factory for creating instances of WordDetailViewModel.
     * Provides the necessary use case dependency.
     */
    class Factory(
        private val getWordUseCase: GetWordUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordDetailViewModel::class.java)) {
                return WordDetailViewModel(getWordUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 