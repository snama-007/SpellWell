package com.wordwell.libwwgenai.repository

import com.wordwell.libwwgenai.api.GenAIService
import com.wordwell.libwwgenai.api.GenAIProvider
import com.wordwell.libwwgenai.api.models.GenAIRequest
import com.wordwell.libwwgenai.api.models.GenAIResponse

/**
 * Repository interface for GenAI operations.
 * Provides a clean abstraction layer for GenAI services.
 */
interface GenAIRepository {
    /**
     * Gets the current active GenAI provider.
     * @return The current GenAIProvider
     */
    fun getCurrentProvider(): GenAIProvider

    /**
     * Sets the active GenAI provider.
     * @param provider The provider to set as active
     */
    suspend fun setProvider(provider: GenAIProvider)

    /**
     * Gets the GenAI service for the specified provider.
     * @param provider The provider to get the service for
     * @return The GenAIService instance
     */
    fun getService(provider: GenAIProvider): GenAIService

    /**
     * Generates text using the current provider.
     * @param request The request containing the prompt and parameters
     * @return GenAIResponse containing the generated text
     */
    suspend fun generateText(request: GenAIRequest): GenAIResponse

    /**
     * Generates an image using the current provider.
     * @param request The request containing the image generation prompt
     * @return GenAIResponse containing the generated image URLs
     */
    suspend fun generateImage(request: GenAIRequest): GenAIResponse

    /**
     * Checks if the current provider is available.
     * @return Boolean indicating if the service is available
     */
    suspend fun isAvailable(): Boolean
} 