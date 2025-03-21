package com.wordwell.libwwmw.domain.usecases

import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving cached words
 */
class GetCachedWordsUseCase(
    private val repository: DictionaryRepository
) {
    operator fun invoke(): Flow<List<Word>> = repository.getCachedWords()
} 