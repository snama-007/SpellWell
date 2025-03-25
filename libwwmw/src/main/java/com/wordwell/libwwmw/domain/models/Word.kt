package com.wordwell.libwwmw.domain.models

import com.wordwell.libwwmw.utils.Constants

/**
 * Word represents a dictionary word with its definitions and phonetics.
 * It is a data class used to encapsulate word-related information.
 * @property id Unique identifier for the word
 * @property word The actual word text
 * @property phonetics List of phonetic representations
 * @property definitions List of word definitions
 * @property timestamp When the word was last fetched/updated
 * @property audioFilePath Local path to the audio file
 * @property audioDownloadStatus Status of audio download
 */
data class Word(
    val id: String,
    val word: String,
    val phonetics: List<Phonetic>,
    val definitions: List<Definition>,
    val timestamp: Long = System.currentTimeMillis(),
    val audioFilePath: String? = null,
    val audioDownloadStatus: Int = Constants.DOWNLOAD_STATUS_PENDING
) {
    /**
     * Checks if this word has an available audio pronunciation.
     * @return true if audio is downloaded and ready to play
     */
    fun hasAudio(): Boolean = 
        audioFilePath != null && audioDownloadStatus == Constants.DOWNLOAD_STATUS_COMPLETED
    
    /**
     * Gets the first available audio URL from phonetics.
     * @return URL to the audio file or null if none available
     */
    fun getAudioUrl(): String? = 
        phonetics.firstOrNull { !it.audioUrl.isNullOrBlank() }?.audioUrl
}

/**
 * Phonetic represents the phonetic representation of a word.
 * @property text IPA (International Phonetic Alphabet) text
 * @property audioUrl URL to pronunciation audio file
 */
data class Phonetic(
    val text: String,
    val audioUrl: String? = null
)

/**
 * Definition represents the definition of a word.
 * @property partOfSpeech The grammatical category (noun, verb, etc.)
 * @property meaning The actual definition text
 * @property examples Usage examples
 */
data class Definition(
    val partOfSpeech: String,
    val meaning: String,
    val examples: List<String> = emptyList()
) 