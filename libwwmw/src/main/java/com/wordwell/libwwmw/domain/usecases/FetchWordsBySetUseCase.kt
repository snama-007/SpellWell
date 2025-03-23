package com.wordwell.libwwmw.domain.usecases

import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching words using the selected strategy.
 * This use case abstracts the strategy selection and word fetching operations.
 */
class FetchWordsBySetUseCase(
    private val repository: DictionaryRepository
) {
    /**
     * Fetches words using the selected strategy.
     * @param setName Name of the set to associate the words with
     * @param words List of words to fetch
     * @param useWorkManager Whether to use WorkManager for fetching data
     * @return Flow of fetched words
     */
    operator suspend fun invoke(
        setName: String,
        words: List<String>,
    ): Flow<DictionaryFetchResult<List<Word>>> {
        return repository.getWordsBySetName(setName, words)
    }

    /**
     * Fetches words by set name using the selected strategy.
     * @param setName Name of the set to fetch words for
     * @param useWorkManager Whether to use WorkManager for fetching data
     * @return Flow of fetched words
     */
    operator suspend fun invoke(
        setName: String,
    ): Flow<DictionaryFetchResult<List<Word>>> = repository.getWordsBySetName(setName)
} 