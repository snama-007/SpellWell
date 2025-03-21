package com.wordwell.spellbee.ui.screens.learn

import androidx.lifecycle.ViewModel
import com.wordwell.spellbee.data.model.Word
import com.wordwell.spellbee.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {
    
    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> = _words.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    init {
        // In a real app, this would be a suspend function called from a viewModelScope
        _words.value = wordRepository.getAllWords()
    }

    fun nextWord() {
        if (_currentWordIndex.value < (_words.value.size - 1)) {
            _currentWordIndex.value = _currentWordIndex.value + 1
        }
    }

    fun previousWord() {
        if (_currentWordIndex.value > 0) {
            _currentWordIndex.value = _currentWordIndex.value - 1
        }
    }

    fun getCurrentWord(): Word? {
        return _words.value.getOrNull(_currentWordIndex.value)
    }
} 