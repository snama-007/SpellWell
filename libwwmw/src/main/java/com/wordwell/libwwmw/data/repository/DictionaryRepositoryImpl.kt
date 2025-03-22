package com.wordwell.libwwmw.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wordwell.libwwmw.data.api.DictionaryMapper
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.domain.models.DictionaryResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

// DictionaryRepositoryImpl is responsible for managing data operations related to dictionary words.
// It handles fetching words from the network, storing them in the local database, and retrieving cached words.
class DictionaryRepositoryImpl(
    private val api: MerriamWebsterApi, // API client for network operations
    private val db: DictionaryDatabase, // Local database for caching words
    private val context: Context, // Application context for network checks
    private val apiKey: String // API key for authentication
) : DictionaryRepository {

    private val dao = db.wordDao() // Data Access Object for database operations

    /**
     * Fetches a single word from the network and stores it locally.
     * If the word is found, it is saved in the local database.
     * @param word The word to fetch.
     * @return A Flow emitting the result of the fetch operation as a DictionaryResult.
     */
    override suspend fun getWord(word: String): Flow<DictionaryResult<Word>> = flow {
        emit(getWordFromNetwork(word))
    }

    /**
     * Retrieves a list of words (set) from the local database.
     * Limits the result to 10 words to manage cache size.
     * @return A Flow emitting a list of cached words.
     */
    override fun getCachedWords(): Flow<List<Word>> = flow {
        dao.getAllWords().collect { wordEntities ->
            val words = wordEntities.map { entity ->
                Word(
                    id = entity.id,
                    word = entity.word,
                    phonetics = entity.phonetics,
                    definitions = entity.definitions,
                    timestamp = entity.timestamp
                )
            }.take(10)
            emit(words)
        }
    }

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
    private suspend fun getWordFromNetwork(word: String): DictionaryResult<Word> {
        return try {
            val response = api.getWord(word.lowercase())
            if (response.isNotEmpty()) {
                val domainWord = DictionaryMapper.toDomain(response[0], word)
                dao.insertWord(
                    WordEntity(
                        id = domainWord.id,
                        word = domainWord.word,
                        phonetics = domainWord.phonetics,
                        definitions = domainWord.definitions,
                        timestamp = System.currentTimeMillis()
                    )
                )
                dao.keepRecentWords() // Ensure cache limit
                DictionaryResult.Success(domainWord)
            } else {
                DictionaryResult.Error("Word not found")
            }
        } catch (e: HttpException) {
            DictionaryResult.Error("Network error: ${e.message}", e)
        } catch (e: IOException) {
            DictionaryResult.Error("Network error: ${e.message}", e)
        }
    }

    /**
     * Retrieves the current size of the cache.
     * @return The number of words currently stored in the cache.
     */
    override suspend fun getCacheSize(): Int = dao.getWordCount()
}
