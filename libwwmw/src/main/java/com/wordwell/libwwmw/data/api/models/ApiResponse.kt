package com.wordwell.libwwmw.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * API response model for Merriam-Webster dictionary
 */
// ApiResponse models represent the structure of the response from the Merriam-Webster API.
// These data classes are used to parse the JSON response from the API.
data class DictionaryResponse(
    @SerializedName("meta") val meta: Meta, // Metadata about the word
    @SerializedName("hwi") val headwordInfo: HeadwordInfo, // Headword information
    @SerializedName("def") val definitions: List<ApiDefinition>, // List of definitions
    @SerializedName("fl") val functionalLabel: String, // Part of speech
)

data class Meta(
    @SerializedName("id") val id: String // Unique identifier for the word
)

data class HeadwordInfo(
    @SerializedName("prs") val pronunciations: List<Pronunciation>? = null // List of pronunciations
)

data class Pronunciation(
    @SerializedName("mw") val text: String, // Phonetic text
    @SerializedName("sound") val sound: Sound? = null // Sound information
)

data class Sound(
    @SerializedName("audio") val audio: String? = null // Audio file name
)

data class ApiDefinition(
    @SerializedName("sseq") val sensesSequence: List<List<List<Any>>> // Complex structure for meanings
)
