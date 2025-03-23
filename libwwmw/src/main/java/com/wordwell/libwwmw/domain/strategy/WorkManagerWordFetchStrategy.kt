package com.wordwell.libwwmw.domain.strategy

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wordwell.libwwmw.data.db.dao.SetDao
import com.wordwell.libwwmw.data.db.dao.WordDao
import com.wordwell.libwwmw.domain.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * WorkManager strategy for fetching word data.
 * Uses WorkManager to perform background operations.
 */
class WorkManagerWordFetchStrategy @Inject constructor(
    private val context: Context,
    private val wordDao: WordDao,
    private val setDao: SetDao
) : WordFetchStrategy {

    override suspend fun fetchWords(setName: String, words: List<String>,): Flow<List<Word>> = flow {
        // Create or update set
        setDao.createSetIfNotExists(setName)

        // Schedule work for fetching words
        val workRequest = OneTimeWorkRequestBuilder<WordFetchWorker>()
            .setInputData(
                workDataOf(
                "words" to words.toTypedArray(),
                "setName" to setName
                )
            ).build()

        WorkManager.getInstance(context).enqueue(workRequest)

        // Emit words from database as they are updated
        wordDao.getAllWords().map { wordEntities ->
            wordEntities.map {
                Word(
                    it.id,
                    word = it.word,
                    phonetics = it.phonetics,
                    definitions = it.definitions,
                    timestamp = it.timestamp
                )
            }
        }.collect { words ->
            // Update word count after fetching
            setDao.updateUniqueWordCount(setName)
            emit(words)
        }
    }

    override suspend fun fetchWordsBySetName(setName: String): Flow<List<Word>> = flow {
        // Verify set exists
        if (!setDao.setExists(setName)) {
            //throw IllegalArgumentException("Set with name $setName does not exist")
            return@flow
        }

        // Emit words associated with the set
        wordDao.getWordsBySetName(setName).map { wordEntities ->
            wordEntities.map {
                Word(
                    it.id,
                    word = it.word,
                    phonetics = it.phonetics,
                    definitions = it.definitions,
                    timestamp = it.timestamp
            )}
        }.collect { words ->
            emit(words)
        }
    }

    override suspend fun fetchWord(word: String): Flow<Word> {
        return flow{}
    }
}