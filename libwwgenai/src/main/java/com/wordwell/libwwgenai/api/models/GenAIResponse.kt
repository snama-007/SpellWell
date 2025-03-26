package com.wordwell.libwwgenai.api.models

import com.wordwell.libwwgenai.api.GenAIProvider

/**
 * Sealed class representing different types of responses from GenAI services.
 */
sealed class GenAIResponse {
    /**
     * Represents a successful text generation response.
     * @property text The generated text
     * @property provider The provider that generated the response
     * @property usage Token usage information
     * @property finishReason The reason why generation finished
     */
    data class TextResponse(
        val text: String,
        val provider: GenAIProvider,
        val usage: TokenUsage,
        val finishReason: String
    ) : GenAIResponse()

    /**
     * Represents a successful image generation response.
     * @property imageUrls List of generated image URLs
     * @property provider The provider that generated the response
     * @property usage Token usage information
     */
    data class ImageResponse(
        val imageUrls: List<String>,
        val provider: GenAIProvider,
        val usage: TokenUsage
    ) : GenAIResponse()

    /**
     * Represents an error response.
     * @property error The error message
     * @property provider The provider that encountered the error
     * @property code Optional error code
     */
    data class ErrorResponse(
        val error: String,
        val provider: GenAIProvider,
        val code: String? = null
    ) : GenAIResponse()
}

/**
 * Data class representing token usage information.
 * @property promptTokens Number of tokens in the prompt
 * @property completionTokens Number of tokens in the completion
 * @property totalTokens Total number of tokens used
 */
data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
) {
    init {
        require(promptTokens >= 0) { "Prompt tokens cannot be negative" }
        require(completionTokens >= 0) { "Completion tokens cannot be negative" }
        require(totalTokens >= 0) { "Total tokens cannot be negative" }
        require(totalTokens == promptTokens + completionTokens) { 
            "Total tokens must equal prompt tokens plus completion tokens" 
        }
    }
} 