package com.wordwell.libwwmw.domain.strategy

import com.wordwell.libwwmw.domain.models.Word
import kotlinx.coroutines.flow.Flow

/**
 * Strategy interface for fetching word data.
 * Implementations can use different methods such as WorkManager or Coroutines.
 */
interface WordFetchStrategy {

    /**
     * Fetches word data based on the provided list of words and set name.
     * Creates the set if it doesn't exist and updates the word count.
     * @param words List of words to fetch data for.
     * @param setName Name of the set to associate the fetched data with.
     * @return A Flow emitting a list of Word objects.
     */
    suspend fun fetchWords(setName: String, words: List<String>): Flow<List<Word>>

    /**
     * Fetches word data based on the provided set name.
     * @param setName Name of the set to fetch data for.
     * @return A Flow emitting a list of Word objects associated with the set name.
     */
    suspend fun fetchWordsBySetName(setName: String): Flow<List<Word>>
    
    /**
     * Fetches data for a single word.
     * @param word The word to fetch data for.
     * @return A Flow emitting a DictionaryResult containing the word data or error information.
     */
    suspend fun fetchWord(word: String): Flow<Word>
}