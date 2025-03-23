package com.wordwell.libwwmw.utils

import android.util.Log

object LogUtils {

    private fun getCallerFileName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        // The stack trace index might need adjustment depending on the environment
        val callerStackTraceElement = stackTrace[4] // Adjust index if necessary
        return callerStackTraceElement.fileName ?: "UnknownFile"
    }

    fun logWithTimer(message: String, startTime: Long) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val fileName = getCallerFileName()
        val tag = fileName
        Log.d(tag, "$message (Elapsed time: ${elapsedTime}ms)")
    }

    fun log(message: String) {
        val fileName = getCallerFileName()
        val tag = fileName
        Log.d(tag, message)
    }
}