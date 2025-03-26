package com.wordwell.libwwgenai.providers

import com.wordwell.libwwgenai.api.GenAIService
import com.wordwell.libwwgenai.api.GenAIProvider
import com.wordwell.libwwgenai.api.models.GenAIRequest
import com.wordwell.libwwgenai.api.models.GenAIResponse
import com.wordwell.libwwgenai.api.models.TokenUsage

/**
 * Base abstract class for GenAI providers.
 * Provides common functionality and enforces provider-specific implementations.
 */
abstract class BaseGenAIProvider(
    protected val provider: GenAIProvider
) : GenAIService {

    override suspend fun generateText(request: GenAIRequest): GenAIResponse {
        require(request.provider == provider) {
            "Request provider ${request.provider} does not match this provider $provider"
        }
        return try {
            doGenerateText(request)
        } catch (e: Exception) {
            GenAIResponse.ErrorResponse(
                error = e.message ?: "Unknown error occurred",
                provider = provider
            )
        }
    }

    override suspend fun generateImage(request: GenAIRequest): GenAIResponse {
        require(request.provider == provider) {
            "Request provider ${request.provider} does not match this provider $provider"
        }
        require(provider.supportsImageGeneration) {
            "Provider $provider does not support image generation"
        }
        return try {
            doGenerateImage(request)
        } catch (e: Exception) {
            GenAIResponse.ErrorResponse(
                error = e.message ?: "Unknown error occurred",
                provider = provider
            )
        }
    }

    /**
     * Provider-specific implementation of text generation.
     * @param request The request containing the prompt and parameters
     * @return GenAIResponse containing the generated text
     */
    protected abstract suspend fun doGenerateText(request: GenAIRequest): GenAIResponse

    /**
     * Provider-specific implementation of image generation.
     * @param request The request containing the image generation prompt
     * @return GenAIResponse containing the generated image URLs
     */
    protected abstract suspend fun doGenerateImage(request: GenAIRequest): GenAIResponse

    /**
     * Creates a TokenUsage object with the given values.
     * @param promptTokens Number of tokens in the prompt
     * @param completionTokens Number of tokens in the completion
     * @return TokenUsage object
     */
    protected fun createTokenUsage(promptTokens: Int, completionTokens: Int): TokenUsage {
        return TokenUsage(
            promptTokens = promptTokens,
            completionTokens = completionTokens,
            totalTokens = promptTokens + completionTokens
        )
    }
} 