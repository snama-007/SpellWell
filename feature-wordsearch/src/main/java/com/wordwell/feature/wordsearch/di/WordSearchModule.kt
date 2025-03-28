package com.wordwell.feature.wordsearch.di

import android.content.Context
import com.wordwell.feature.wordsearch.WordSearchFeature
import com.wordwell.feature.wordsearch.data.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordSearchModule {
    
    @Provides
    @Singleton
    fun provideWordSearchFeature(): WordSearchFeature {
        return WordSearchFeature()
    }
    
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
} 