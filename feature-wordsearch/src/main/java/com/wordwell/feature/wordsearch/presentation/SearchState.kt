package com.wordwell.feature.wordsearch.presentation

import com.wordwell.feature.wordsearch.domain.model.SearchResult

/**
 * Represents the UI state of the search feature
 */
data class SearchState(
    val query: String = "",
    val isWordSetMode: Boolean = true,
    val suggestions: List<String> = emptyList(),
    val isVoiceInputActive: Boolean = false,
    val isVoiceListening: Boolean = false,
    val voiceRecognizedText: String = "",
    val voiceError: String? = null,
    val searchResults: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchHistory: List<String> = emptyList(),
    val userPreferences: UserSearchPreferences = UserSearchPreferences()
)

/**
 * User preferences for search
 */
data class UserSearchPreferences(
    val favoriteCategories: List<String> = emptyList(),
    val recentCategories: List<String> = emptyList(),
    val showRecentsFirst: Boolean = true,
    val voiceInputEnabled: Boolean = true
) 