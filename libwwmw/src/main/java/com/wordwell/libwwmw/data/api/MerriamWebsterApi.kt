package com.wordwell.libwwmw.data.api

import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Merriam-Webster Dictionary API service
 */
interface MerriamWebsterApi {
    @GET("api/v3/references/sd2/json/{word}")
    suspend fun getWord(
        @Path("word") word: String
    ): List<DictionaryResponse>

    companion object {
        const val BASE_URL = "https://dictionaryapi.com/"
    }
} 