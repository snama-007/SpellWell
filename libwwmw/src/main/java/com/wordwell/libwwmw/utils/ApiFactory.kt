package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.api.MockMerriamWebsterApi

/**
 * Factory for creating API service instances
 */
object ApiFactory {
    
    /**
     * Creates an instance of MerriamWebsterApi
     * 
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