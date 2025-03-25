package com.wordwell.libwwmw.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.utils.Constants
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
     * Inserts multiple word entities into the database.
     * Replaces any existing entities with the same IDs.
     * @param words The list of WordEntity objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

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

    /**
     * Retrieves word entities associated with a specific set name.
     * @param setName The name of the set to query
     * @return A Flow emitting a list of WordEntities associated with the set
     */
    @Query("SELECT * FROM words WHERE setName = :setName ORDER BY timestamp DESC")
    fun getWordsBySetName(setName: String): Flow<List<WordEntity>>

    /**
     * Updates the audio file path and download status for a word.
     * @param wordId The ID of the word to update
     * @param audioFilePath The local path to the audio file
     * @param status The new download status
     */
    @Query("UPDATE words SET audioFilePath = :audioFilePath, audioDownloadStatus = :status WHERE id = :wordId")
    suspend fun updateAudioInfo(wordId: String, audioFilePath: String, status: Int = Constants.DOWNLOAD_STATUS_COMPLETED)
    
    // Add this method to observe changes to a specific word
    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordFlow(id: String): Flow<WordEntity?>

    /**
     * Updates the download status for a word.
     * @param wordId The ID of the word to update
     * @param status The new download status
     */
    @Query("UPDATE words SET audioDownloadStatus = :status WHERE id = :wordId")
    suspend fun updateAudioStatus(wordId: String, status: Int)

    /**
     * Retrieves words that have audio URLs but haven't been downloaded yet.
     * @param limit Maximum number of words to retrieve (default 10)
     * @return List of WordEntities with pending audio downloads
     */
    @Query("""
        SELECT * FROM words 
        WHERE audioUrl IS NOT NULL 
        AND audioUrl != '' 
        AND audioDownloadStatus = ${Constants.DOWNLOAD_STATUS_PENDING}
        LIMIT :limit
    """)
    suspend fun getWordsWithPendingAudio(limit: Int = 10): List<WordEntity>
} 