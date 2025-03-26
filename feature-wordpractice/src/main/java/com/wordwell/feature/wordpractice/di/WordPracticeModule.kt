package com.wordwell.feature.wordpractice.di

import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WordPracticeModule {

    @Provides
    @Singleton
    fun provideCachedWordsViewModel(wordWellServer: WordWellServer): CachedWordsViewModel {
        return wordWellServer.cachedWordsViewModelFactory.create(CachedWordsViewModel::class.java)
    }
} 