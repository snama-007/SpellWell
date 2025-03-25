package com.wordwell.libwwmw.utils

import android.util.Log

/**
 * Utility class for logging with consistent tags and formatting.
 */
object LogUtils {
    private const val DEFAULT_TAG = "WordWell"
    private const val MAX_TAG_LENGTH = 23 

    /**
     * Logs a message with the calling class name as the tag.
     * 
     * @param message The message to log
     * @param isError Whether to log as error (true) or info (false)
     * @param throwable Optional throwable to include in the log
     */
    fun log(message: String, isError: Boolean = false, throwable: Throwable? = null) {
        val tag = getCallerClassName()
        
        if (isError) {
            Log.e(tag, message, throwable)
        } else {
            Log.d(tag, message)
        }
    }

    /**
     * Gets the calling class name to use as a tag.
     * 
     * @return The calling class name, truncated if necessary
     */
    private fun getCallerClassName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        
        // Find the first class that isn't LogUtils or Thread
        val callerElement = stackTrace.firstOrNull { element ->
            val className = element.className
            !className.contains("LogUtils") && 
            !className.contains("Thread") &&
            !className.contains("dalvik.system") &&
            !className.contains("java.lang")
        }
        
        // Extract simple class name from the full class name
        val fullClassName = callerElement?.className ?: return DEFAULT_TAG
        val simpleClassName = fullClassName.substringAfterLast('.')
        
        // Truncate if necessary to meet Android's tag length limit
        return if (simpleClassName.length > MAX_TAG_LENGTH) {
            simpleClassName.substring(0, MAX_TAG_LENGTH)
        } else {
            simpleClassName
        }
    }
}