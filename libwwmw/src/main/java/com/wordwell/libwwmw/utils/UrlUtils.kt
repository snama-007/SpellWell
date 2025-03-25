package com.wordwell.libwwmw.utils

import java.net.URL

/**
 * Utility functions for URL operations.
 */
object UrlUtils {
    
    /**
     * Extracts the file name from a URL.
     * 
     * @param url The URL string to extract the file name from
     * @return The file name extracted from the URL, or null if extraction fails
     */
    fun extractFileName(url: String?): String? {
        if (url.isNullOrBlank()) return null
        
        return try {
            val path = URL(url).path
            val fileName = path.substring(path.lastIndexOf('/') + 1)
            
            // Handle query parameters if present
            if (fileName.contains('?')) {
                fileName.substring(0, fileName.indexOf('?'))
            } else {
                fileName
            }
        } catch (e: Exception) {
            LogUtils.log("Failed to extract file name from URL: $url", isError = true)
            null
        }
    }
    
    /**
     * Extracts the file name from a URL and ensures it has the specified extension.
     * 
     * @param url The URL string to extract the file name from
     * @param defaultExtension The extension to add if the file name doesn't have one (without dot)
     * @return The file name with proper extension, or null if extraction fails
     */
    fun extractFileNameWithExtension(url: String?, defaultExtension: String): String? {
        val fileName = extractFileName(url) ?: return null
        
        return if (fileName.contains('.')) {
            fileName
        } else {
            "$fileName.$defaultExtension"
        }
    }
}
