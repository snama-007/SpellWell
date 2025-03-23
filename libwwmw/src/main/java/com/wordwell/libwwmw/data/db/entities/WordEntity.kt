package com.wordwell.libwwmw.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wordwell.libwwmw.domain.models.Definition
import com.wordwell.libwwmw.domain.models.Phonetic

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val word: String,
    val phonetics: List<Phonetic>,
    val definitions: List<Definition>,
    val timestamp: Long,
    val setName: String
)

/**
 * Type converters for Room to handle complex objects
 */
class WordConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromPhonetics(phonetics: List<Phonetic>): String = gson.toJson(phonetics)

    @TypeConverter
    fun toPhonetics(json: String): List<Phonetic> = gson.fromJson(
        json,
        object : TypeToken<List<Phonetic>>() {}.type
    )

    @TypeConverter
    fun fromDefinitions(definitions: List<Definition>): String = gson.toJson(definitions)

    @TypeConverter
    fun toDefinitions(json: String): List<Definition> = gson.fromJson(
        json,
        object : TypeToken<List<Definition>>() {}.type
    )
} 