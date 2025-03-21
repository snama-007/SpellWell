package com.wordwell.libwwmw.data.repository

import android.content.Context
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.domain.repository.DictionaryRepository
import com.wordwell.libwwmw.utils.NetworkUtils

/**
 * Factory for creating DictionaryRepository instances
 */
object DictionaryRepositoryFactory {
    @Volatile
    private var INSTANCE: DictionaryRepository? = null

    fun getInstance(
        context: Context,
        apiKey: String
    ): DictionaryRepository {
        return INSTANCE ?: synchronized(this) {
            DictionaryRepositoryImpl(
                api = NetworkUtils.createApiService(apiKey),
                db = DictionaryDatabase.getInstance(context),
                context = context.applicationContext,
                apiKey = apiKey
            ).also { INSTANCE = it }
        }
    }
} 