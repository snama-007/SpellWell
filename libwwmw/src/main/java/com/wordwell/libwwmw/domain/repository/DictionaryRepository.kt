package com.wordwell.libwwmw.domain.repository

import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for dictionary operations
 * Implements offline-first approach with caching
 */
interface DictionaryRepository {
    /**
     * Fetches word information with offline-first approach
     * @param word The word to look up
     * @return Flow of DictionaryResult containing Word data
     */
    suspend fun getWord(word: String): Flow<DictionaryFetchResult<Word>>

    /**
     * Retrieves all cached words
     * @return Flow of cached words list
     */
    suspend fun getCachedWords(): Flow<DictionaryFetchResult<List<Word>>>

    /**
     * Retrieves all words associated with a specific set
     * @param setName The name of the set to retrieve words for
     * @return Flow of words list belonging to the specified set
     */
    suspend fun getWordsBySetName(setName: String): Flow<DictionaryFetchResult<List<Word>>>

    /**
     * Retrieves all words associated with a specific set
     * @param setName The name of the set to retrieve words for
     * @return Flow of words list belonging to the specified set
     */
    suspend fun getWordsBySetName(setName: String, words: List<String>): Flow<DictionaryFetchResult<List<Word>>>

    /**
     * Clears the word cache
     */
    suspend fun clearCache()

    /**
     * Gets the cache size
     * @return Number of words in cache
     */
    suspend fun getCacheSize(): Int
} 