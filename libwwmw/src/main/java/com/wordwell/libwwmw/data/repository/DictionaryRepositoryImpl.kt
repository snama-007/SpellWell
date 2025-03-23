package com.wordwell.libwwmw.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wordwell.libwwmw.data.api.DictionaryMapper
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.domain.strategy.DataFetchStrategySelector
import com.wordwell.libwwmw.utils.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

// DictionaryRepositoryImpl is responsible for managing data operations related to dictionary words.
class DictionaryRepositoryImpl(
    private val api: MerriamWebsterApi, // API client for network operations
    private val db: DictionaryDatabase, // Local database for caching words
    private val context: Context, // Application context for network checks
    private val apiKey: String // API key for authentication
) : DictionaryRepository {

    private val dao = db.wordDao()
    private val networkFetchStrategy = DataFetchStrategySelector(context).selectStrategy()

    /**
     * Fetches a single word from the network and stores it locally.
     * If the word is found, it is saved in the  local database.
     * @param word The word to fetch.
     * @return A Flow emitting the result of the fetch operation as a DictionaryResult.
     */
    override suspend fun getWord(word: String): Flow<DictionaryFetchResult<Word>> = flow {
        emit(getWordFromNetwork(word))
    }

    /**
     * Retrieves a list of words (set) from the local database.
     * Limits the result to 10 words to manage cache size.
     * @return A Flow emitting a list of cached words.
     */
    override suspend fun getCachedWords(): Flow<DictionaryFetchResult<List<Word>>> = flow {
        dao.getAllWords().collect { wordEntities ->
            val words = wordEntities.map { entity ->
                Word(
                    id = entity.id,
                    word = entity.word,
                    phonetics = entity.phonetics,
                    definitions = entity.definitions,
                    timestamp = entity.timestamp,
                )
            }.take(10)
            emit(DictionaryFetchResult.Success(words))
        }
    }
    /**
      * Retrieves the current set of words associated with a specific set.
      * @param setName The name of the set to retrieve words for.
      * @return A Flow emitting a list of words associated with the set.
      */
    override suspend fun getWordsBySetName(setName: String): Flow<DictionaryFetchResult<List<Word>>> = flow {
        networkFetchStrategy.fetchWordsBySetName(setName).collect { result ->
            emit(DictionaryFetchResult.Success(result))
        }
    }
    override suspend fun getWordsBySetName(
        setName: String,
        words: List<String>
    ): Flow<DictionaryFetchResult<List<Word>>> = flow {
        // First check if we have this set in the local database
        val localWords = dao.getWordsBySetName(setName)
        localWords.collect{ words ->
            if (words.isNotEmpty()) {
                // If we have words locally, map and return them
                LogUtils.log("Set $setName cached: ${words.size}")
                val domainWords = words.map { entity ->
                    Word(
                        id = entity.id,
                        word = entity.word,
                        phonetics = entity.phonetics,
                        definitions = entity.definitions,
                        timestamp = entity.timestamp
                    )
                }
                emit(DictionaryFetchResult.Success(domainWords))            }
        }

        if (isNetworkAvailable()) {
            // If not available locally but network is available, fetch from network
            try {
                LogUtils.log("Set $setName fetching from MW: ${words.size} words")

                // Use the network strategy to fetch words
                networkFetchStrategy.fetchWords(setName, words).collect { result ->
                    emit(DictionaryFetchResult.Success(result))
                }
            } catch (e: Exception) {
                emit(DictionaryFetchResult.Error("Failed to fetch words for set: ${e.message}", e))
            }
        } else {
            // No local data and no network
            emit(DictionaryFetchResult.Error(NETWORK_UNAVAILABLE_ERROR, Exception()))
        }
    }
    /**
     * Retrieves the current size of the cache.
     * @return The number of words currently stored in the cache.
     */
    override suspend fun getCacheSize(): Int = dao.getWordCount()

    /**
     * Clears the cache if it exceeds 3 sets of 10 words.
     * This ensures that the cache does not grow indefinitely.
     */
    override suspend fun clearCache() {
        if (dao.getWordCount() > 30) {
            dao.clearWords()
        }
    }

    /**
     * Checks if the network is available.
     * Utilizes the ConnectivityManager to determine network status.
     * @return True if the network is available, false otherwise.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Fetches a word from the network using the Merriam-Webster API.
     * If successful, the word is stored in the local database.
     * @param word The word to fetch.
     * @return A DictionaryResult containing the word or an error message.
     */
    private suspend fun getWordFromNetwork(word: String): DictionaryFetchResult<Word> {
        if (!isNetworkAvailable()) {
            return DictionaryFetchResult.Error(NETWORK_UNAVAILABLE_ERROR)
        }
        return try {
            val response = api.getWord(word.lowercase())
            if (response.isNotEmpty()) {
                val domainWord = mapResponseToDomain(response[0], word)
                storeWordInDatabase(domainWord)
                DictionaryFetchResult.Success(domainWord)
            } else {
                DictionaryFetchResult.Error(WORD_NOT_FOUND_ERROR)
            }
        } catch (e: HttpException) {
            DictionaryFetchResult.Error(NETWORK_ERROR_MESSAGE + e.message, e)
        } catch (e: IOException) {
            DictionaryFetchResult.Error(NETWORK_ERROR_MESSAGE + e.message, e)
        }
    }

    private fun mapResponseToDomain(response: DictionaryResponse, word: String): Word {
        return DictionaryMapper.toDomain(response, word)
    }

    private suspend fun storeWordInDatabase(word: Word) {
        dao.insertWord(
            WordEntity(
                id = word.id,
                word = word.word,
                phonetics = word.phonetics,
                definitions = word.definitions,
                timestamp = System.currentTimeMillis(),
                setName = word.word
            )
        )
        dao.keepRecentWords() // Ensure cache limit
    }

    companion object {
        // Define constants for error messages
        private const val NETWORK_UNAVAILABLE_ERROR = "Network is not available"
        private const val WORD_NOT_FOUND_ERROR = "Word not found"
        private const val NETWORK_ERROR_MESSAGE = "Network error: "

    }
}

