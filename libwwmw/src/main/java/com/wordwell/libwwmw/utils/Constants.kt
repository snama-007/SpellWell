package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.BuildConfig

// Constants object holds constant values used throughout the application
object Constants {
    const val MW_API_KEY = "514fdf81-c2ac-42ff-bac6-18634a1a7c81" // Default API key for Merriam-Webster
    const val IS_DUMMY_NETWORK = true // Flag to indicate if dummy network should be used
    const val DATABASE_NAME = "dictionary.db" // Name of the SQLite database

    // Audio
    const val AUDIO_DIR_NAME = "audio"
    const val AUDIO_FILE_EXTENSION = ".mp3"
    
    // Download Status
    const val DOWNLOAD_STATUS_PENDING = 0
    const val DOWNLOAD_STATUS_IN_PROGRESS = 1
    const val DOWNLOAD_STATUS_COMPLETED = 2
    const val DOWNLOAD_STATUS_FAILED = 3
    
    // WorkManager Tags
    const val AUDIO_DOWNLOAD_WORK_TAG = "audio_download_work"

    private var encryptedApiKey: String? = null // Encrypted API key storage

    /**
     * Initializes the API key by encrypting it for secure storage.
     * @param context The application context
     */
    fun initializeApiKey(context: Context) {
        if (encryptedApiKey == null) {
            encryptedApiKey = SecurityUtils.encryptApiKey(
                context,
                MW_API_KEY
            )
        }
    }

    /**
     * Initializes the API key with a provided key and encrypts it for secure storage.
     * @param context The application context
     * @param apiKey The API key to initialize
     */
    fun initializeApiKey(context: Context, apiKey: String) {
        if (encryptedApiKey == null) {
            encryptedApiKey = SecurityUtils.encryptApiKey(
                context,
                apiKey
            )
        }
    }

    /**
     * Retrieves the decrypted API key for use in network requests.
     * @param context The application context
     * @return The decrypted API key
     */
    fun getApiKey(context: Context): String {
        return if (encryptedApiKey != null) {
            SecurityUtils.decryptApiKey(context, encryptedApiKey!!)
        } else {
            BuildConfig.MERRIAM_WEBSTER_API_KEY
        }
    }
} 