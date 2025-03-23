package com.wordwell.libwwmw.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wordwell.libwwmw.data.db.entities.SetEntity
import kotlinx.coroutines.flow.Flow

/**
 * SetDao defines the data access operations for the SetEntity in the Room database.
 * It provides methods for querying, inserting, and managing set data.
 */
@Dao
interface SetDao {
    /**
     * Retrieves all sets ordered by name.
     * @return A Flow emitting a list of SetEntities
     */
    @Query("SELECT * FROM sets ORDER BY name ASC")
    fun getAllSets(): Flow<List<SetEntity>>

    /**
     * Inserts a set entity into the database.
     * Replaces any existing entity with the same ID.
     * @param set The SetEntity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity)

    /**
     * Retrieves a set by its name.
     * @param name The name of the set to query
     * @return A Flow emitting the SetEntity if found
     */
    @Query("SELECT * FROM sets WHERE name = :name LIMIT 1")
    fun getSetByName(name: String): Flow<SetEntity?>

    /**
     * Updates the word count for a specific set.
     * @param name The name of the set
     * @param count The new word count
     */
    @Query("UPDATE sets SET numberOfWords = :count WHERE name = :name")
    suspend fun updateWordCount(name: String, count: Int)

    /**
     * Checks if a set exists by name.
     * @param name The name of the set to check
     * @return True if the set exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM sets WHERE name = :name)")
    suspend fun setExists(name: String): Boolean

    /**
     * Creates a new set if it doesn't exist.
     * @param name The name of the set
     * @param initialWordCount The initial word count for the set
     */
    @Transaction
    suspend fun createSetIfNotExists(name: String, initialWordCount: Int = 0) {
        if (!setExists(name)) {
            insertSet(
                SetEntity(
                    id = generateSetId(name),
                    name = name,
                    numberOfWords = initialWordCount
                )
            )
        }
    }

    /**
     * Updates the word count for a set based on unique words.
     * @param name The name of the set
     */
    @Query("""
        UPDATE sets 
        SET numberOfWords = (
            SELECT COUNT(DISTINCT word) 
            FROM words 
            WHERE setName = :name
        )
        WHERE name = :name
    """)
    suspend fun updateUniqueWordCount(name: String)
}

private fun generateSetId(name: String): String {
    return "set_${name.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}"
} 