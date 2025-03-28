package com.wordwell.libwwmw.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * API response model for Merriam-Webster dictionary
 */
// ApiResponse models represent the structure of the response from the Merriam-Webster API.
// These data classes are used to parse the JSON response from the API.
data class DictionaryResponse(
    @SerializedName("meta") val meta: Meta? = null,
    @SerializedName("hwi") val headwordInfo: HeadwordInfo? = null,
    @SerializedName("def") val definitions: List<ApiDefinition>? = null,
    @SerializedName("fl") val functionalLabel: String? = null,
    @SerializedName("history") val history: History? = null,
    @SerializedName("shortdef") val shortDefinitions: List<String>? = null,
    @SerializedName("ins") val inflections: List<Inflection>? = null
) {
    fun isValid(): Boolean {
        return meta != null && headwordInfo != null && definitions != null
    }
}

data class Meta(
    @SerializedName("id") val id: String? = null,
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("offensive") val offensive: Boolean = false,
    @SerializedName("section") val section: String? = null,
    @SerializedName("stems") val stems: List<String>? = null,
    @SerializedName("src") val source: String? = null,
    @SerializedName("sort") val sort: String? = null
)

data class HeadwordInfo(
    @SerializedName("hw") val text: String? = null,
    @SerializedName("prs") val pronunciations: List<Pronunciation>? = null
)

data class Pronunciation(
    @SerializedName("mw") val text: String? = null,
    @SerializedName("sound") val sound: Sound? = null
)

data class Sound(
    @SerializedName("audio") val audio: String? = null
)

data class ApiDefinition(
    @SerializedName("sseq") val sensesSequence: List<List<List<Any>>>? = null
)

data class History(
    @SerializedName("pl") val label: String? = null,
    @SerializedName("pt") val text: List<List<String>>? = null
)

data class Inflection(
    @SerializedName("if") val inf: String? = null
)