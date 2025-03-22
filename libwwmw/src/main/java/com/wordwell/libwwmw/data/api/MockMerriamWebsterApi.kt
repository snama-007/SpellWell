package com.wordwell.libwwmw.data.api

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Mock implementation of MerriamWebsterApi for testing and development
 * Fetches data from a mock server instead of the real API
 */
class MockMerriamWebsterApi : MerriamWebsterApi {
    
    companion object {
        const val MOCK_BASE_URL = "https://run.mocky.io/v3/8e50d749-8dd1-4549-b358-817f4d7e8733"
    }
    
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val gson = Gson()

    /**
     * Fetches mock word data from the mock server
     */
    override suspend fun getWord(word: String): List<DictionaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(MOCK_BASE_URL)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    throw Exception("Failed to fetch mock data: ${response.code}")
                }
                
                val responseBody = response.body?.string() ?: throw Exception("Empty response body")
                val jsonObject = Gson().fromJson(responseBody, JsonObject::class.java)
                val jsonArray = jsonObject.getAsJsonArray("words")[getDictIndex()].asJsonArray
                jsonArray.map { Gson().fromJson(it.toString(), DictionaryResponse::class.java) }

            } catch (e: Exception) {
                // Fallback to hardcoded mock data if the mock server is unavailable
                parseDictionaryResponse()
            }
        }
    }
    fun getDictIndex(): Int {
        return (0..4).random()
    }

    private fun parseDictionaryResponse(): List<DictionaryResponse> {
        val gson = Gson()
        val jsonArray = gson.fromJson(jsonString, JsonArray::class.java)
        return jsonArray.map { gson.fromJson(it, DictionaryResponse::class.java) }
    }

    val jsonString = "[[{\"def\":[{\"sseq\":[[[{\"sense\":{\"dt\":[[[\"text\",\"{bc}an automatic electronic machine that can store and process data\"]]]}]}]]}]],\"fl\":\"noun\",\"hwi\":{\"hw\":\"com*put*er\",\"prs\":[{\"mw\":\"k\u0259m-\u02c8py\u00fc-t\u0259r\",\"sound\":{\"audio\":\"comput06\"}}]},\"meta\":{\"id\":\"computer\",\"offensive\":false,\"section\":\"alpha\",\"sort\":\"031204000\",\"src\":\"sd2\",\"stems\":[\"computer\",\"computerdom\",\"computerdoms\",\"computerless\",\"computerlike\",\"computers\"],\"uuid\":\"1bb89bc6-5d39-4a58-a882-6d97f05c81a9\"},\"shortdef\":[\"an automatic electronic machine that can store and process data\"]},{\"def\":[{\"sseq\":[[[{\"sense\":{\"dt\":[[[\"text\",\"{bc}a computer designed for an individual user\"]]]}]}]]}]],\"fl\":\"noun\",\"hwi\":{\"hw\":\"personal computer\"},\"meta\":{\"id\":\"personal computer\",\"offensive\":false,\"section\":\"alpha\",\"sort\":\"160481000\",\"src\":\"sd2\",\"stems\":[\"personal computer\"],\"uuid\":\"8463fb3d-b147-4f27-b368-e552a1cee5ce\"},\"shortdef\":[\"a computer designed for an individual user\"]}]]"
}