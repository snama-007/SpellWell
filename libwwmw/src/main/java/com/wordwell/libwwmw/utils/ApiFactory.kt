package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.api.MockMerriamWebsterApi

// ApiFactory is responsible for creating instances of API services.
// It provides a method to create the Merriam-Webster API service, with an option to use a mock implementation.
object ApiFactory {
    
    /**
     * Creates an instance of MerriamWebsterApi.
     * Allows for the use of a mock implementation for testing purposes.
     * @param context Application context
     * @param useMock Whether to use the mock implementation
     * @return An implementation of MerriamWebsterApi
     */
    fun createApiService(context: Context, useMock: Boolean = false): MerriamWebsterApi {
        return if (useMock) {
            MockMerriamWebsterApi()
        } else {
            NetworkUtils.createApiService(context)
        }
    }
}