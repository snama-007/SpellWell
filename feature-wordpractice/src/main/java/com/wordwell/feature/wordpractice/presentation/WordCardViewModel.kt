package com.wordwell.feature.wordpractice.presentation

import android.media.MediaPlayer
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

    private val _currentCardNumber = MutableLiveData<Int>()
    val currentCardNumber: LiveData<Int> = _currentCardNumber

    private val _totalCards = MutableLiveData<Int>()
    val totalCards: LiveData<Int> = _totalCards

    private var currentIndex = 0
    private lateinit var resultWords: WWResultData.WordResult
    private var mediaPlayer: MediaPlayer? = null

    fun initializeWithWordResult(wordResult: WWResultData.WordResult) {
        resultWords = wordResult
        _totalCards.value = resultWords.words.size
        if (resultWords.words.isNotEmpty()) {
            _currentWord.value = resultWords.words[currentIndex]
            _currentCardNumber.value = currentIndex + 1
        }
    }

    fun moveToNextWord(): Boolean {
        return if (currentIndex < resultWords.words.size - 1) {
            currentIndex++
            _currentWord.value = resultWords.words[currentIndex]
            _currentCardNumber.value = currentIndex + 1
            true
        } else {
            false
        }
    }

    fun moveToPreviousWord(): Boolean {
        return if (currentIndex > 0) {
            currentIndex--
            _currentWord.value = resultWords.words[currentIndex]
            _currentCardNumber.value = currentIndex + 1
            true
        } else {
            false
        }
    }

    fun playPronunciation(word: Word) {
        word.getAudioUrl()?.let { url ->
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    prepare()
                    start()
                    setOnCompletionListener {
                        release()
                        mediaPlayer = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
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