package com.wordwell.libwwgenai.api

/**
 * Enum class representing different GenAI providers.
 * Each provider has associated configuration and capabilities.
 */
enum class GenAIProvider {
    OPENAI {
        override val displayName: String = "OpenAI"
        override val supportsTextGeneration: Boolean = true
        override val supportsImageGeneration: Boolean = true
        override val maxTokens: Int = 4096
    },
    DEEPSEEK {
        override val displayName: String = "DeepSeek"
        override val supportsTextGeneration: Boolean = true
        override val supportsImageGeneration: Boolean = false
        override val maxTokens: Int = 8192
    },
    GROK {
        override val displayName: String = "Grok"
        override val supportsTextGeneration: Boolean = true
        override val supportsImageGeneration: Boolean = true
        override val maxTokens: Int = 4096
    };

    abstract val displayName: String
    abstract val supportsTextGeneration: Boolean
    abstract val supportsImageGeneration: Boolean
    abstract val maxTokens: Int

    companion object {
        fun fromString(value: String): GenAIProvider? = values().find { 
            it.name.equals(value, ignoreCase = true) 
        }
    }
} 