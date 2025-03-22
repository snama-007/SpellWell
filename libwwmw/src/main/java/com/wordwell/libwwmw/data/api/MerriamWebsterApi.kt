package com.wordwell.libwwmw.data.api

import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Merriam-Webster Dictionary API service
 */
interface MerriamWebsterApi {
    /**
     * Fetches the dictionary entry for a given word.
     * @param word The word to look up
     * @return A list of DictionaryResponse objects containing the word's details
     */
    @GET("api/v3/references/sd2/json/{word}")
    suspend fun getWord(
        @Path("word") word: String
    ): List<DictionaryResponse>

    companion object {
        const val BASE_URL = "https://dictionaryapi.com/" // Base URL for the Merriam-Webster API
        const val BASE_AUDIO_URL_FULL = "https://media.merriam-webster.com/audio/prons/en/us/mp3/" // Base URL for audio files
    }
}
