package com.wordwell.libwwmw.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wordwell.libwwmw.data.api.DictionaryMapper
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.entities.toWordEntity
import com.wordwell.libwwmw.domain.audio.AudioDownloadManager
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.models.WordSet
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.domain.strategy.DataFetchStrategySelector
import com.wordwell.libwwmw.utils.Constants
import com.wordwell.libwwmw.utils.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

// DictionaryRepositoryImpl is responsible for managing data operations related to dictionary words.
class DictionaryRepositoryImpl @Inject constructor(
    private val api: MerriamWebsterApi, // API client for network operations
    private val db: DictionaryDatabase, // Local database for caching words
    private val context: Context, // Application context for network checks
    private val apiKey: String, // API key for authentication
    private val audioDownloadManager: AudioDownloadManager? = null // For managing audio downloads
) : DictionaryRepository {

    private val dao = db.wordDao()
    private val setDao = db.setDao()
    private val networkFetchStrategy = DataFetchStrategySelector(context, audioDownloadManager).selectStrategy()
    /**
     * Fetches a single word from the network and stores it locally.
     * If the word is found, it is saved in the local database and its audio is queued for download.
     * @param word The word to fetch.
     * @return A Flow emitting the result of the fetch operation as a DictionaryResult.
     */
    override suspend fun getWord(word: String): Flow<DictionaryFetchResult<Word>> = flow {
        // Get initial result from network
        val result = getWordFromNetwork(word)
        
        // Emit initial result
        emit(result)
        
        // Queue audio download if result is successful
        if (result is DictionaryFetchResult.Success) {
            result.data.getAudioUrl()?.let { audioUrl ->
                // Schedule audio download in a non-blocking way
                audioDownloadManager?.scheduleAudioDownload(result.data.id, audioUrl)
                
                // Set up a flow to observe changes to this word in the database
                dao.getWordFlow(result.data.id).collect { wordEntity ->
                    if (wordEntity != null && 
                        (wordEntity.audioDownloadStatus == Constants.DOWNLOAD_STATUS_COMPLETED || 
                         wordEntity.audioDownloadStatus == Constants.DOWNLOAD_STATUS_FAILED)) {
                        // Emit updated word when download completes or fails
                        emit(DictionaryFetchResult.Success(wordEntity.toWord()))
                    }
                }
            }
        }
    }
    /**
     * Retrieves a list of words (set) from the local database.
     * Limits the result to 10 words to manage cache size.
     * @return A Flow emitting a list of cached words.
     */
    override suspend fun getCachedWords(): Flow<DictionaryFetchResult<List<Word>>> = flow {
        LogUtils.log("Getting cached words")
        dao.getAllWords().map { wordEntities ->
            wordEntities.map { entity ->
                entity.toWord()
            }.take(10)
        }.collect { words ->
            // Process pending audio downloads
           // audioDownloadManager?.processPendingDownloads()
            emit(DictionaryFetchResult.Success(words))
        }
    }

    override suspend fun getCachedSets(): Flow<DictionaryFetchResult<List<WordSet>>>  = flow{
        LogUtils.log("Getting cached words")
        setDao.getAllSets().map { setEntities ->
            setEntities.map { entity ->
                entity.toWordSet(
                    id = entity.id,
                    name = entity.name,
                    numberOfWords = entity.numberOfWords
                )
            }
        }.collect { sets ->
            emit(DictionaryFetchResult.Success(sets))
        }
    }

    /**
     * Retrieves the current set of words associated with a specific set.
     * @param setName The name of the set to retrieve words for.
     * @return A Flow emitting a list of words associated with the set.
     */
    override suspend fun getWordsBySetName(setName: String): Flow<DictionaryFetchResult<List<Word>>> = flow {
        networkFetchStrategy.fetchWordsBySetName(setName).collect { result ->
            // Process pending audio downloads
            emit(DictionaryFetchResult.Success(result))
        }
    }
    /**
      * Retrieves the current set of words associated with a specific set.
      */
    override suspend fun getWordsBySetName(
        setName: String,
        words: List<String>
    ): Flow<DictionaryFetchResult<List<Word>>> = flow {
        // First check if we have this set in the local database
        val localWords = dao.getWordsBySetName(setName)
        if (localWords.first().isNotEmpty()) {
            localWords.map { wordEntities ->
                wordEntities.map { entity ->
                    entity.toWord()
                }
            }.collect { domainWords ->
                LogUtils.log("Set $setName cached: ${domainWords.size}")
                emit(DictionaryFetchResult.Success(domainWords))
            }
            return@flow // Return after processing local words
        }

        if (isNetworkAvailable()) {
            // If not available locally but network is available, fetch from network
            try {
                LogUtils.log("Set $setName fetching from MW: ${words.size}")

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
            return DictionaryFetchResult.Error(NETWORK_UNAVAILABLE_ERROR, Exception())
        }
        
        return try {
            val response = api.getWord(word.lowercase())
            if(response.isEmpty()) DictionaryFetchResult.Error(WORD_NOT_FOUND_ERROR, Exception())
            val responseWord = response.first()
            if (responseWord.isValid()) {
                val domainWord = mapResponseToDomain(responseWord, word)
                storeWordInDatabase(domainWord)
                DictionaryFetchResult.Success(domainWord)
            } else {
                DictionaryFetchResult.Error(WORD_NOT_FOUND_ERROR, Exception())
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
        dao.insertWord(word.toWordEntity(word.word))
        dao.keepRecentWords() // Ensure cache limit
    }

    companion object {
        // Define constants for error messages
        private const val NETWORK_UNAVAILABLE_ERROR = "Network is not available"
        private const val WORD_NOT_FOUND_ERROR = "Word not found"
        private const val NETWORK_ERROR_MESSAGE = "Network error: "
    }
}