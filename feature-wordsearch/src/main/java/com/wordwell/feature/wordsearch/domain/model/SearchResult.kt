package com.wordwell.feature.wordsearch.domain.model

/**
 * Represents the result of a search operation
 */
sealed class SearchResult {
    data class WordResult(
        val id: String,
        val word: String,
        val definition: String,
        val partOfSpeech: String,
        val phoneticSpelling: String
    ) : SearchResult()
    
    data class SetResult(
        val id: String,
        val name: String,
        val description: String,
        val wordCount: Int,
        val category: String
    ) : SearchResult()
} 