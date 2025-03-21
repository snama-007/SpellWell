package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.BuildConfig

object Constants {
    private var encryptedApiKey: String? = null

    fun initializeApiKey(context: Context) {
        if (encryptedApiKey == null) {
            encryptedApiKey = SecurityUtils.encryptApiKey(
                context,
                BuildConfig.MERRIAM_WEBSTER_API_KEY
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