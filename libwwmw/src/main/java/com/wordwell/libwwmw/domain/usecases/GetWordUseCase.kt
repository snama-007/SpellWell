package com.wordwell.libwwmw.domain.usecases

import com.wordwell.libwwmw.domain.models.DictionaryResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for looking up word definitions
 * @property repository The dictionary repository implementation
 */
class GetWordUseCase(private val repository: DictionaryRepository) {
    /**
     * Executes the use case
     * @param word The word to look up
     * @return Flow of DictionaryResult containing Word data
     */
    suspend operator fun invoke(word: String): Flow<DictionaryResult<Word>> = 
        repository.getWord(word.trim().lowercase())
} 