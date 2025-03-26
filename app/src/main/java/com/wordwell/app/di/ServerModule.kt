package com.wordwell.app.di

import android.content.Context
import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {

    @Provides
    @Singleton
    fun provideWordWellServer(
        @ApplicationContext context: Context
    ): WordWellServer {
        return WordWellServer.getInstance(
            context = context,
            apiKey = Constants.MW_API_KEY,
            useMockApi = true // Use mock data for development
        )
    }
} 