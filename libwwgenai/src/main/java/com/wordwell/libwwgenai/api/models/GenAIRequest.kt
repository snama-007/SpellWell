package com.wordwell.libwwgenai.api.models

import com.wordwell.libwwgenai.api.GenAIProvider

/**
 * Data class representing a request to a GenAI service.
 * @property prompt The main prompt text for the AI
 * @property provider The AI provider to use
 * @property maxTokens Maximum number of tokens to generate
 * @property temperature Temperature parameter for controlling randomness (0.0 to 1.0)
 * @property topP Top-p parameter for nucleus sampling
 * @property frequencyPenalty Frequency penalty for reducing repetition
 * @property presencePenalty Presence penalty for encouraging diversity
 * @property stopSequences Optional sequences where generation should stop
 * @property additionalParams Additional provider-specific parameters
 */
data class GenAIRequest(
    val prompt: String,
    val provider: GenAIProvider,
    val maxTokens: Int = provider.maxTokens,
    val temperature: Float = 0.7f,
    val topP: Float = 1.0f,
    val frequencyPenalty: Float = 0.0f,
    val presencePenalty: Float = 0.0f,
    val stopSequences: List<String> = emptyList(),
    val additionalParams: Map<String, Any> = emptyMap()
) {
    init {
        require(temperature in 0f..1f) { "Temperature must be between 0 and 1" }
        require(topP in 0f..1f) { "Top-p must be between 0 and 1" }
        require(maxTokens > 0) { "Max tokens must be positive" }
        require(maxTokens <= provider.maxTokens) { 
            "Max tokens cannot exceed provider limit of ${provider.maxTokens}" 
        }
    }
} 