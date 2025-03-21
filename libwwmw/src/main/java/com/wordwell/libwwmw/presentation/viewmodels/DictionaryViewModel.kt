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

class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _wordState = MutableStateFlow<DictionaryResult<Word>>(DictionaryResult.Loading)
    val wordState: StateFlow<DictionaryResult<Word>> = _wordState

    private val _cachedWords = MutableStateFlow<List<Word>>(emptyList())
    val cachedWords: StateFlow<List<Word>> = _cachedWords

    fun lookupWord(word: String) {
        viewModelScope.launch {
            repository.getWord(word).collect { result ->
                _wordState.value = result
            }
        }
    }

    fun loadCachedWords() {
        viewModelScope.launch {
            repository.getCachedWords().collect { words ->
                _cachedWords.value = words
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            loadCachedWords()
        }
    }

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