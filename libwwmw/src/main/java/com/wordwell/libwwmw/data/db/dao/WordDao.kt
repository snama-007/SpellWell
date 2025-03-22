package com.wordwell.libwwmw.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordwell.libwwmw.data.db.entities.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    fun getWord(word: String): Flow<WordEntity?>

    @Query("SELECT * FROM words ORDER BY timestamp DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("DELETE FROM words")
    suspend fun clearWords()

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