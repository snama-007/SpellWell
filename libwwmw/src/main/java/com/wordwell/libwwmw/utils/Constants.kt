package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.BuildConfig

object Constants {
    const val MW_API_KEY = "514fdf81-c2ac-42ff-bac6-18634a1a7c81"
    const val IS_DUMMY_NETWORK =  true

    private var encryptedApiKey: String? = null

    fun initializeApiKey(context: Context) {
        if (encryptedApiKey == null) {
            encryptedApiKey = SecurityUtils.encryptApiKey(
                context,
                BuildConfig.MERRIAM_WEBSTER_API_KEY
            )
        }
    }

    fun initializeApiKey(context: Context, apiKey: String) {
        if (encryptedApiKey == null) {
            encryptedApiKey = SecurityUtils.encryptApiKey(
                context,
                apiKey
            )
        }
    }

    fun getApiKey(context: Context): String {
        return if (encryptedApiKey != null) {
            SecurityUtils.decryptApiKey(context, encryptedApiKey!!)
        } else {
            BuildConfig.MERRIAM_WEBSTER_API_KEY
        }
    }
} 