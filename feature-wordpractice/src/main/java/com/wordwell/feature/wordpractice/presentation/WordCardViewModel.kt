package com.wordwell.feature.wordpractice.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wordwell.libwwmw.domain.models.WWResultData
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordCardViewModel @Inject constructor(
    private val cachedWordsViewModel: CachedWordsViewModel
) : ViewModel() {

    private val _currentWord = MutableLiveData<Word>()
    val currentWord: LiveData<Word> = _currentWord

    private var currentIndex = 0
    private lateinit var resultWords: WWResultData.WordResult

    init{
        // Initialize the ViewModel
        //LogUtils.log(wordPracticeViewModel.currentWords.toString())
        // Observe words from CachedWordsViewModel
        /*viewModelScope.launch {
            cachedWordsViewModel.uiState.collect { result ->
                when (result) {
                    is UiState.Success -> {
                        resultWords = result.data as WWResultData.WordResult
                        if (resultWords.words.isNotEmpty()) {
                            _currentWord.value = resultWords.words[currentIndex]
                        }
                    }
                    else -> {}
                }
            }
        }*/
    }

    fun initializeWithWordResult(wordResult: WWResultData.WordResult) {
        resultWords = wordResult
        if (resultWords.words.isNotEmpty()) {
            _currentWord.value = resultWords.words[currentIndex]
        }
    }

    fun moveToNextWord() {
        if (currentIndex < resultWords.words.size - 1) {
            currentIndex++
            _currentWord.value = resultWords.words[currentIndex]
        }
    }

    fun moveToPreviousWord() {
        if (currentIndex > 0) {
            currentIndex--
            _currentWord.value = resultWords.words[currentIndex]
        }
    }

    fun playPronunciation(word: Word) {
        // This will be implemented with actual audio playback
        // You can use word.pronunciationUrl or similar field
    }

    fun toggleFavorite(word: Word) {
        // This will be implemented with libwwmw interface
    }

    class Factory(
        private val cachedWordsViewModel: CachedWordsViewModel
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordCardViewModel::class.java)) {
                return WordCardViewModel(cachedWordsViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 