package com.wordwell.feature.wordpractice.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordwell.libwwmw.domain.models.WWResultData
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.UiState
import com.wordwell.libwwmw.utils.LogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordPracticeViewModel @Inject constructor(
    private val cachedWordsViewModel: CachedWordsViewModel
) : ViewModel() {
    
    private val _wordSets = MutableLiveData<WWResultData.WordSetResult>()
    val wordSets: LiveData<WWResultData.WordSetResult> = _wordSets

    private val _currentWords = MutableLiveData<WWResultData.WordResult>()
    val currentWords: LiveData<WWResultData.WordResult> = _currentWords

    init {
        // Temporary mock data for sets
        cachedWordsViewModel.fetchAllSets()
        viewModelScope.launch {
            cachedWordsViewModel.uiState.collect{result ->
                when(result){
                    is UiState.Success -> {
                        LogUtils.log("Data : $result.data.toString()")
                        if( result.data is WWResultData.WordSetResult)
                            _wordSets.value = result.data as WWResultData.WordSetResult
                        else
                            _currentWords.value = result.data as WWResultData.WordResult?
                    }
                    is UiState.Error ->
                        LogUtils.log("Error : $result.message")
                    else -> {}
                }
            }
        }
    }

    fun loadWordsForSet(setName: String) {
        cachedWordsViewModel.fetchWordsBySetName(setName)
    }

    class Factory(
        private val cachedWordsViewModelFactory: CachedWordsViewModel.Factory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordPracticeViewModel::class.java)) {
                return WordPracticeViewModel(cachedWordsViewModelFactory.create(CachedWordsViewModel::class.java)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 