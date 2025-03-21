package com.wordwell.libwwmw.domain.repository

import com.wordwell.libwwmw.domain.models.DictionaryResult
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
    suspend fun getWord(word: String): Flow<DictionaryResult<Word>>

    /**
     * Retrieves all cached words
     * @return Flow of cached words list
     */
    fun getCachedWords(): Flow<List<Word>>

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