package com.wordwell.libwwmw.domain.strategy

import android.content.Context
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.repository.DictionaryRepositoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * StrategySelector provides a way to choose between different word fetch strategies.
 */
interface StrategySelector {
    /**
     * Selects a word fetch strategy based on the given parameters.
     * @param useWorkManager Whether to use WorkManager for fetching data
     * @return The selected WordFetchStrategy
     */
    fun selectStrategy(useWorkManager: Boolean = true): WordFetchStrategy
}

/**
 * Default implementation of StrategySelector.
 */
class DataFetchStrategySelector(private val context: Context) : StrategySelector {
    override fun selectStrategy(useWorkManager: Boolean): WordFetchStrategy {
        val db = DictionaryDatabase.getInstance(context)
        return if (useWorkManager) {
            WorkManagerWordFetchStrategy(
                context,
                wordDao = db.wordDao(),
                setDao = db.setDao()
            )
        } else {
            CoroutineWordFetchStrategy(
                wordDao = db.wordDao(),
                setDao = db.setDao(),
                coroutineScope = CoroutineScope(Dispatchers.IO),
                wordRepository = DictionaryRepositoryFactory.getInstance(context,"", false)
            )
        }
    }
} 