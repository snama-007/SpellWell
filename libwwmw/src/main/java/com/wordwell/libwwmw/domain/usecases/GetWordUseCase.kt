package com.wordwell.libwwmw.domain.usecases

import com.wordwell.libwwmw.domain.models.DictionaryResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow

// GetWordUseCase is responsible for executing the logic to fetch word definitions.
// It interacts with the repository to retrieve word data using an offline-first approach.
class GetWordUseCase(private val repository: DictionaryRepository) {
    /**
     * Executes the use case to fetch word definitions.
     * Trims and lowercases the input word before querying the repository.
     * @param word The word to look up
     * @return Flow of DictionaryResult containing Word data
     */
    suspend operator fun invoke(word: String): Flow<DictionaryResult<Word>> = 
        repository.getWord(word.trim().lowercase())
} 