package com.wordwell.libwwgenai.di

import com.wordwell.libwwgenai.api.GenAIService
import com.wordwell.libwwgenai.api.GenAIProvider
import com.wordwell.libwwgenai.providers.OpenAIProvider
import com.wordwell.libwwgenai.providers.DeepSeekProvider
import com.wordwell.libwwgenai.providers.GrokProvider
import com.wordwell.libwwgenai.repository.GenAIRepository
import com.wordwell.libwwgenai.repository.GenAIRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing GenAI-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object GenAIModule {

    @Provides
    @Singleton
    fun provideGenAIServices(
        openAIProvider: OpenAIProvider,
        deepSeekProvider: DeepSeekProvider,
        grokProvider: GrokProvider
    ): Map<GenAIProvider, GenAIService> = mapOf(
        GenAIProvider.OPENAI to openAIProvider,
        GenAIProvider.DEEPSEEK to deepSeekProvider,
        GenAIProvider.GROK to grokProvider
    )

    @Provides
    @Singleton
    fun provideGenAIRepository(
        services: Map<GenAIProvider, GenAIService>
    ): GenAIRepository = GenAIRepositoryImpl(services)
} 