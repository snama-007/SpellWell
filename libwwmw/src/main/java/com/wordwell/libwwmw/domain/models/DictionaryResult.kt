package com.wordwell.libwwmw.domain.models

/**
 * Represents the different states of dictionary operations
 * @param T The type of data being wrapped
 */
sealed class DictionaryResult<out T> {
    data class Success<T>(val data: T) : DictionaryResult<T>()
    data class Error(val message: String, val cause: Exception? = null) : DictionaryResult<Nothing>()
    data object Loading : DictionaryResult<Nothing>()
} 