package com.wordwell.libwwmw.domain.strategy

import androidx.lifecycle.ViewModel
import com.wordwell.libwwmw.data.db.dao.SetDao
import com.wordwell.libwwmw.data.db.dao.WordDao
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Coroutine strategy for fetching word data.
 * Uses a ViewModel to manage coroutine scope and perform operations.
 */
class CoroutineWordFetchStrategy @Inject constructor(
    private val wordDao: WordDao,
    private val setDao: SetDao,
    private val wordRepository: DictionaryRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel(), WordFetchStrategy {

    /* override suspend fun fetchData(words: List<String>, setName: String): Flow<List<Word>> = flow {
        // Create or update set
        setDao.createSetIfNotExists(setName)

        // Launch coroutine for each word
        coroutineScope.launch {
            words.forEach { word ->
                try {
                    val fetchedWord = wordRepository.getWord(word)
                    fetchedWord.collect{
                        when(it){
                            is DictionaryResult.Success -> {
                                val wordEntity = WordEntity(
                                    id = it.data.id,
                                    word = it.data.word,
                                    phonetics = it.data.phonetics,
                                    definitions = it.data.definitions,
                                    timestamp = it.data.timestamp,
                                    setName = setName
                                )
                                wordDao.insertWord(wordEntity)
                            }
                            is DictionaryResult.Error -> {
                                // Log error but continue with other words
                                Log.e("CoroutineWordFetchStrategy", "Error fetching word: $word", it.cause)
                            }
                            else ->{}
                        }
                    }
                } catch (e: Exception) {
                    // Log error but continue with other words
                    Log.e("CoroutineWordFetchStrategy", "Error fetching word: $word", e)
                }
            }
        }

        // Emit words from database as they are updated
        wordDao.getWordsBySetName(setName).map { wordEntities ->
            wordEntities.map { it.toWord() }
        }.collect { words ->
            // Update word count after fetching
            setDao.updateUniqueWordCount(setName)
            emit(words)
        }
    }

    override suspend fun fetchDataBySetName(setName: String): Flow<List<Word>> = flow {
        // Verify set exists
        if (!setDao.setExists(setName)) {
            throw IllegalArgumentException("Set with name $setName does not exist")
        }

        // Emit words associated with the set
        wordDao.getWordsBySetName(setName).map { wordEntities ->
            wordEntities.map { it.toWord() }
        }.collect { words ->
            emit(words)
        }
    }*/

    override suspend fun fetchWords(setName: String, words: List<String>): Flow<List<Word>> {
        return flow {}
    }

    override suspend fun fetchWordsBySetName(setName: String): Flow<List<Word>> {
        return flow {}
    }

    override suspend fun fetchWord(word: String): Flow<Word> {
        return flow {}
    }

} 