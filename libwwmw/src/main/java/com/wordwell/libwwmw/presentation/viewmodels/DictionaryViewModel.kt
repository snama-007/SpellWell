package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.DictionaryResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// DictionaryViewModel manages the UI-related data for dictionary operations.
// It interacts with the repository to fetch word details and cached words.
class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _wordState = MutableStateFlow<DictionaryResult<Word>>(DictionaryResult.Loading) // Holds the state of the word lookup operation
    val wordState: StateFlow<DictionaryResult<Word>> = _wordState // Exposes the word state as a read-only StateFlow

    private val _cachedWords = MutableStateFlow<List<Word>>(emptyList()) // Holds the list of cached words
    val cachedWords: StateFlow<List<Word>> = _cachedWords // Exposes the cached words as a read-only StateFlow

    /**
     * Initiates a word lookup operation.
     * Fetches the word details from the repository and updates the word state.
     * @param word The word to look up
     */
    fun lookupWord(word: String) {
        viewModelScope.launch {
            repository.getWord(word).collect { result ->
                _wordState.value = result
            }
        }
    }

    /**
     * Loads the cached words from the repository.
     * Updates the cached words state with the retrieved list.
     */
    fun loadCachedWords() {
        viewModelScope.launch {
            repository.getCachedWords().collect { words ->
                _cachedWords.value = words
            }
        }
    }

    /**
     * Clears the word cache in the repository.
     * Reloads the cached words after clearing the cache.
     */
    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            loadCachedWords()
        }
    }

    /**
     * Factory for creating instances of DictionaryViewModel.
     * Provides the necessary repository dependency.
     */
    class Factory(private val repository: DictionaryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DictionaryViewModel::class.java)) {
                return DictionaryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 