package com.wordwell.libwwmw.domain.usecases

import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow

/**
 * GetCachedWordsUseCase is responsible for executing the logic to retrieve cached words.
 * It interacts with the repository to fetch the list of cached words.
 */
class FetchCachedWordsUseCase(
    private val repository: DictionaryRepository
) {
    /**
     * Executes the use case to retrieve cached words.
     * @return Flow of List containing cached Word data
     */
    suspend operator fun invoke(): Flow<DictionaryFetchResult<List<Word>>> = repository.getCachedWords()
} 