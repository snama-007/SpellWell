package com.wordwell.libwwmw.domain.strategy

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.wordwell.libwwmw.data.api.DictionaryMapper
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.utils.ApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker class for fetching word data.
 * Performs network requests and stores results in the database.
 */
class WordFetchWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val words = inputData.getStringArray("words") ?: return@withContext Result.failure()
        val setName = inputData.getString("setName") ?: return@withContext Result.failure()

        val api = ApiFactory.createApiService(context = applicationContext)
        val db = DictionaryDatabase.getInstance(applicationContext)
        val dao = db.wordDao()

        try {
            val wordEntities = words.mapNotNull { word ->
                val response = api.getWord(word)
                if (response.isNotEmpty()) {
                    val domainWord = DictionaryMapper.toDomain(response[0], word)
                    WordEntity(
                        id = domainWord.id,
                        word = domainWord.word,
                        phonetics = domainWord.phonetics,
                        definitions = domainWord.definitions,
                        timestamp = System.currentTimeMillis(),
                        setName = setName
                    )
                } else null
            }

            dao.insertWords(wordEntities)
            val wordsNullable: Array<String?> = words.map { it }.toTypedArray()
            Result.success(Data.Builder().putStringArray("result", wordsNullable).build())
            //Result.success(Data.Builder().putStringArray("result", words).build())
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 