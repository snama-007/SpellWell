package com.wordwell.libwwgenai.repository

import com.wordwell.libwwgenai.api.GenAIService
import com.wordwell.libwwgenai.api.GenAIProvider
import com.wordwell.libwwgenai.api.models.GenAIRequest
import com.wordwell.libwwgenai.api.models.GenAIResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GenAIRepository that manages GenAI services and provider switching.
 */
@Singleton
class GenAIRepositoryImpl @Inject constructor(
    private val services: Map<GenAIProvider, GenAIService>
) : GenAIRepository {

    private var currentProvider: GenAIProvider = GenAIProvider.OPENAI

    override fun getCurrentProvider(): GenAIProvider = currentProvider

    override suspend fun setProvider(provider: GenAIProvider) {
        require(services.containsKey(provider)) { 
            "Provider $provider is not configured" 
        }
        currentProvider = provider
    }

    override fun getService(provider: GenAIProvider): GenAIService {
        return services[provider] ?: throw IllegalArgumentException(
            "Provider $provider is not configured"
        )
    }

    override suspend fun generateText(request: GenAIRequest): GenAIResponse {
        val service = getService(request.provider)
        return try {
            service.generateText(request)
        } catch (e: Exception) {
            GenAIResponse.ErrorResponse(
                error = e.message ?: "Unknown error occurred",
                provider = request.provider
            )
        }
    }

    override suspend fun generateImage(request: GenAIRequest): GenAIResponse {
        val service = getService(request.provider)
        return try {
            service.generateImage(request)
        } catch (e: Exception) {
            GenAIResponse.ErrorResponse(
                error = e.message ?: "Unknown error occurred",
                provider = request.provider
            )
        }
    }

    override suspend fun isAvailable(): Boolean {
        return try {
            getService(currentProvider).isAvailable()
        } catch (e: Exception) {
            false
        }
    }
} 