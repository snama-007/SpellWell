package com.wordwell.libwwmw.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wordwell.libwwmw.domain.models.Definition
import com.wordwell.libwwmw.domain.models.Phonetic
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.utils.Constants

/**
 * Entity class representing a word in the Room database.
 * @property id Unique identifier for the word
 * @property word The actual word text
 * @property phonetics List of phonetic representations
 * @property definitions List of word definitions
 * @property timestamp When the word was last fetched/updated
 * @property setName The set this word belongs to
 * @property audioFilePath Local path to the audio file
 * @property audioDownloadStatus Status of audio download (0=pending, 1=in_progress, 2=completed, 3=failed)
 * @property audioUrl Original URL of the audio file
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val word: String,
    val phonetics: List<Phonetic>,
    val definitions: List<Definition>,
    val timestamp: Long,
    val setName: String,
    val audioFilePath: String? = null,
    val audioDownloadStatus: Int = Constants.DOWNLOAD_STATUS_PENDING,
    val audioUrl: String? = null
) {
    /**
     * Converts this WordEntity to a domain Word model
     * @return Word domain model
     */
    fun toWord(): Word = Word(
        id = id,
        word = word,
        phonetics = phonetics,
        definitions = definitions,
        timestamp = timestamp,
        audioFilePath = audioFilePath,
        audioDownloadStatus = audioDownloadStatus
    )
}

/**
 * Extension function to convert a Word to a WordEntity
 * @param setName The set name to associate with this word
 * @return WordEntity for database storage
 */
fun Word.toWordEntity(setName: String): WordEntity {
    val audioUrl = this.phonetics.firstOrNull { !it.audioUrl.isNullOrBlank() }?.audioUrl
    
    return WordEntity(
        id = id,
        word = word,
        phonetics = phonetics,
        definitions = definitions,
        timestamp = timestamp,
        setName = setName,
        audioUrl = audioUrl,
        audioFilePath = audioFilePath,
        audioDownloadStatus = audioDownloadStatus
    )
}

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