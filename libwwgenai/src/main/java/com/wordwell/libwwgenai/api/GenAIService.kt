package com.wordwell.libwwgenai.api

import com.wordwell.libwwgenai.api.models.GenAIRequest
import com.wordwell.libwwgenai.api.models.GenAIResponse

/**
 * Core interface for GenAI operations.
 * This interface defines the contract for all GenAI providers.
 */
interface GenAIService {
    /**
     * Generates text based on the provided prompt
     * @param request The request containing the prompt and other parameters
     * @return GenAIResponse containing the generated text
     */
    suspend fun generateText(request: GenAIRequest): GenAIResponse

    /**
     * Generates an image based on the provided prompt
     * @param request The request containing the image generation prompt and parameters
     * @return GenAIResponse containing the generated image URL or data
     */
    suspend fun generateImage(request: GenAIRequest): GenAIResponse

    /**
     * Checks if the service is properly configured and available
     * @return Boolean indicating if the service is available
     */
    suspend fun isAvailable(): Boolean
} 