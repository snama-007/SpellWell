package com.wordwell.libwwgenai.providers

import com.wordwell.libwwgenai.api.GenAIProvider
import com.wordwell.libwwgenai.api.models.GenAIRequest
import com.wordwell.libwwgenai.api.models.GenAIResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Grok provider implementation.
 * Handles text and image generation using Grok's API.
 */
@Singleton
class GrokProvider @Inject constructor(
    private val client: OkHttpClient
) : BaseGenAIProvider(GenAIProvider.GROK) {

    private val baseUrl = "https://api.grok.ai/v1"
    private val jsonMediaType = "application/json".toMediaType()

    override suspend fun isAvailable(): Boolean {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/models")
                .header("Authorization", "Bearer ${System.getenv("GROK_API_KEY")}")
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun doGenerateText(request: GenAIRequest): GenAIResponse {
        val jsonBody = JSONObject().apply {
            put("model", "grok-1")
            put("messages", JSONObject().apply {
                put("role", "user")
                put("content", request.prompt)
            })
            put("max_tokens", request.maxTokens)
            put("temperature", request.temperature)
            put("top_p", request.topP)
            if (request.stopSequences.isNotEmpty()) {
                put("stop", request.stopSequences)
            }
        }

        val httpRequest = Request.Builder()
            .url("$baseUrl/chat/completions")
            .header("Authorization", "Bearer ${System.getenv("GROK_API_KEY")}")
            .post(jsonBody.toString().toRequestBody(jsonMediaType))
            .build()

        return client.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Grok API error: ${response.code}")
            }

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val choices = jsonResponse.getJSONArray("choices")
            val message = choices.getJSONObject(0).getJSONObject("message")
            val usage = jsonResponse.getJSONObject("usage")

            GenAIResponse.TextResponse(
                text = message.getString("content"),
                provider = provider,
                usage = createTokenUsage(
                    promptTokens = usage.getInt("prompt_tokens"),
                    completionTokens = usage.getInt("completion_tokens")
                ),
                finishReason = choices.getJSONObject(0).getString("finish_reason")
            )
        }
    }

    override suspend fun doGenerateImage(request: GenAIRequest): GenAIResponse {
        val jsonBody = JSONObject().apply {
            put("prompt", request.prompt)
            put("n", 1)
            put("size", "1024x1024")
        }

        val httpRequest = Request.Builder()
            .url("$baseUrl/images/generations")
            .header("Authorization", "Bearer ${System.getenv("GROK_API_KEY")}")
            .post(jsonBody.toString().toRequestBody(jsonMediaType))
            .build()

        return client.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Grok API error: ${response.code}")
            }

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val data = jsonResponse.getJSONArray("data")
            val imageUrls = List(data.length()) { i ->
                data.getJSONObject(i).getString("url")
            }

            GenAIResponse.ImageResponse(
                imageUrls = imageUrls,
                provider = provider,
                usage = createTokenUsage(
                    promptTokens = 0, // Grok doesn't provide token usage for image generation
                    completionTokens = 0
                )
            )
        }
    }
} 