package com.wordwell.libwwmw.domain.models

/**
 * Represents a word with its definitions and phonetics
 * @property id Unique identifier for the word
 * @property word The actual word text
 * @property phonetics List of phonetic representations
 * @property definitions List of word definitions
 * @property timestamp When the word was last fetched/updated
 */
data class Word(
    val id: String,
    val word: String,
    val phonetics: List<Phonetic>,
    val definitions: List<Definition>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Phonetic representation of a word
 * @property text IPA (International Phonetic Alphabet) text
 * @property audioUrl URL to pronunciation audio file
 */
data class Phonetic(
    val text: String,
    val audioUrl: String? = null
)

/**
 * Definition of a word
 * @property partOfSpeech The grammatical category (noun, verb, etc.)
 * @property meaning The actual definition text
 * @property examples Usage examples
 */
data class Definition(
    val partOfSpeech: String,
    val meaning: String,
    val examples: List<String> = emptyList()
) 