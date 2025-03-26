package com.wordwell.libwwmw.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wordwell.libwwmw.domain.models.WordSet

/**
 * Entity class representing a set of words in the Room database.
 * @property id Unique identifier for the set
 * @property name Name of the set
 * @property numberOfWords Number of unique words in the set
 */
@Entity(tableName = "sets")
data class SetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val numberOfWords: Int = 0
){
    // Factory function to create a WordSet object
    fun toWordSet(id: String, name: String, numberOfWords: Int): WordSet {
        return WordSet(id, name, numberOfWords)
    }
}

