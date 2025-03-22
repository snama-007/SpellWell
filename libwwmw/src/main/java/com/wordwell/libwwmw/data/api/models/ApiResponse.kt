package com.wordwell.libwwmw.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * API response model for Merriam-Webster dictionary
 */
data class DictionaryResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("hwi") val headwordInfo: HeadwordInfo,
    @SerializedName("def") val definitions: List<ApiDefinition>,
    @SerializedName("fl") val functionalLabel: String, // part of speech
)

data class Meta(
    @SerializedName("id") val id: String
)

data class HeadwordInfo(
    @SerializedName("prs") val pronunciations: List<Pronunciation>? = null
)

data class Pronunciation(
    @SerializedName("mw") val text: String,
    @SerializedName("sound") val sound: Sound? = null
)

data class Sound(
    @SerializedName("audio") val audio: String? = null
)

data class ApiDefinition(
    @SerializedName("sseq") val sensesSequence: List<List<List<Any>>> // complex structure for meanings
)
