package com.wordwell.libwwmw.domain.models

/**
 * Represents the different states of dictionary operations
 * @param T The type of data being wrapped
 */
// DictionaryResult represents the different states of dictionary operations.
// It is a sealed class that encapsulates success, error, and loading states.
sealed class DictionaryFetchResult<out T> {
    /**
     * Represents a successful dictionary operation.
     * @param data The data returned from the operation
     */
    data class Success<T>(val data: T) : DictionaryFetchResult<T>()

    /**
     * Represents an error that occurred during a dictionary operation.
     * @param message The error message
     * @param cause The exception that caused the error, if any
     */
    data class Error(val message: String, val cause: Exception? = null) : DictionaryFetchResult<Nothing>()

    /**
     * Represents a loading state during a dictionary operation.
     */
    data object Loading : DictionaryFetchResult<Nothing>()
} 