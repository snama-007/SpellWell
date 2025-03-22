package com.wordwell.libwwmw.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordwell.libwwmw.data.db.entities.WordEntity
import kotlinx.coroutines.flow.Flow

// WordDao defines the data access operations for the WordEntity in the Room database.
// It provides methods for querying, inserting, and managing word data.
@Dao
interface WordDao {
    /**
     * Retrieves a word entity by its word text.
     * @param word The word text to query
     * @return A Flow emitting the WordEntity if found
     */
    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    fun getWord(word: String): Flow<WordEntity?>

    /**
     * Retrieves all word entities ordered by timestamp in descending order.
     * @return A Flow emitting a list of WordEntities
     */
    @Query("SELECT * FROM words ORDER BY timestamp DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    /**
     * Retrieves the count of word entities in the database.
     * @return The number of words in the database
     */
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    /**
     * Inserts a word entity into the database.
     * Replaces any existing entity with the same ID.
     * @param word The WordEntity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    /**
     * Clears all word entities from the database.
     */
    @Query("DELETE FROM words")
    suspend fun clearWords()

    /**
     * Keeps only the most recent 100 word entities in the database.
     * Deletes older entries to maintain the cache size.
     */
    @Query("""
        DELETE FROM words 
        WHERE id NOT IN (
            SELECT id FROM words 
            ORDER BY timestamp DESC 
            LIMIT 100
        )
    """)
    suspend fun keepRecentWords()
} 