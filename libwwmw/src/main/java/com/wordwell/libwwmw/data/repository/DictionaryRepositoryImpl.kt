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
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class DictionaryRepositoryImpl(
    private val api: MerriamWebsterApi,
    private val db: DictionaryDatabase,
    private val context: Context,
    private val apiKey: String
) : DictionaryRepository {

    private val dao = db.wordDao()

    override suspend fun getWord(word: String): Flow<DictionaryResult<Word>> = flow {
        emit(DictionaryResult.Loading)
        
        // First, try to get from cache
        val cachedWord = dao.getWord(word.lowercase())
        
        cachedWord.collect { cached ->
            cached?.let { 
                emit(DictionaryResult.Success(
                    Word(
                        id = it.id,
                        word = it.word,
                        phonetics = it.phonetics,
                        definitions = it.definitions,
                        timestamp = it.timestamp
                    )
                ))
            }
            
            // If online, fetch fresh data
            if (isNetworkAvailable()) {
                try {
                    val response = api.getWord(word.lowercase())
                    if (response.isNotEmpty()) {
                        val domainWord = DictionaryMapper.toDomain(response[0], word)
                        dao.insertWord(WordEntity(
                            id = domainWord.id,
                            word = domainWord.word,
                            phonetics = domainWord.phonetics,
                            definitions = domainWord.definitions,
                            timestamp = System.currentTimeMillis()
                        ))
                        dao.keepRecentWords() // Ensure cache limit
                        emit(DictionaryResult.Success(domainWord))
                    } else {
                        emit(DictionaryResult.Error("Word not found", null))
                    }
                } catch (e: HttpException) {
                    if (cached == null) {
                        emit(DictionaryResult.Error("Network error: ${e.message}", e))
                    }
                } catch (e: IOException) {
                    if (cached == null) {
                        emit(DictionaryResult.Error("Network error: ${e.message}", e))
                    }
                }
            } else if (cached == null) {
                emit(DictionaryResult.Error("No internet connection and no cached data", null))
            }
        }
    }

    override fun getCachedWords(): Flow<List<Word>> = 
        dao.getAllWords().map { entities ->
            entities.map { entity ->
                Word(
                    id = entity.id,
                    word = entity.word,
                    phonetics = entity.phonetics,
                    definitions = entity.definitions,
                    timestamp = entity.timestamp
                )
            }
        }

    override suspend fun clearCache() {
        dao.clearWords()
    }

    override suspend fun getCacheSize(): Int = dao.getWordCount()

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
