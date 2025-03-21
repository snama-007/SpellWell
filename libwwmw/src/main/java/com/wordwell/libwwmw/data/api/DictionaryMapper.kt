package com.wordwell.libwwmw.data.api

import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import com.wordwell.libwwmw.domain.models.Definition
import com.wordwell.libwwmw.domain.models.Phonetic
import com.wordwell.libwwmw.domain.models.Word

/**
 * Maps API responses to domain models
 */
object DictionaryMapper {
    fun toDomain(response: DictionaryResponse, searchedWord: String): Word {
        return Word(
            id = response.meta.id,
            word = searchedWord,
            phonetics = response.headwordInfo.pronunciations?.map { pron ->
                Phonetic(
                    text = pron.text,
                    audioUrl = pron.sound?.audio?.let { audio ->
                        "${MerriamWebsterApi.BASE_URL}audio/prons/en/us/mp3/$audio.mp3"
                    }
                )
            } ?: emptyList(),
            definitions = response.definitions.map { def ->
                Definition(
                    partOfSpeech = def.functionalLabel,
                    meaning = extractMeaning(def.sensesSequence),
                    examples = extractExamples(def.sensesSequence)
                )
            }
        )
    }

    private fun extractMeaning(senseSeq: List<List<List<Any>>>): String {
        // Simplified meaning extraction - you might want to enhance this based on API response structure
        return senseSeq.firstOrNull()?.firstOrNull()?.getOrNull(1)?.toString() ?: ""
    }

    private fun extractExamples(senseSeq: List<List<List<Any>>>): List<String> {
        // Simplified example extraction - you might want to enhance this based on API response structure
        return emptyList()
    }
} 