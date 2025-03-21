package com.wordwell.spellbee.di

import com.wordwell.spellbee.data.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideWordRepository(): WordRepository {
        return WordRepository()
    }
} 