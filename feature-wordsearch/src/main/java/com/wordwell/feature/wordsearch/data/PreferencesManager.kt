package com.wordwell.feature.wordsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wordwell.feature.wordsearch.presentation.UserSearchPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "word_search_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    // Preference keys
    companion object {
        val FAVORITE_CATEGORIES = stringPreferencesKey("favorite_categories")
        val RECENT_CATEGORIES = stringPreferencesKey("recent_categories")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
        val SHOW_RECENTS_FIRST = booleanPreferencesKey("show_recents_first")
        val VOICE_INPUT_ENABLED = booleanPreferencesKey("voice_input_enabled")
    }
    
    // Get user preferences as a Flow
    val userPreferencesFlow: Flow<UserSearchPreferences> = context.dataStore.data
        .map { preferences ->
            val favoriteCategories = preferences[FAVORITE_CATEGORIES]?.split(",") ?: emptyList()
            val recentCategories = preferences[RECENT_CATEGORIES]?.split(",") ?: emptyList()
            val showRecentsFirst = preferences[SHOW_RECENTS_FIRST] ?: true
            val voiceInputEnabled = preferences[VOICE_INPUT_ENABLED] ?: true
            
            UserSearchPreferences(
                favoriteCategories = favoriteCategories.filter { it.isNotBlank() },
                recentCategories = recentCategories.filter { it.isNotBlank() },
                showRecentsFirst = showRecentsFirst,
                voiceInputEnabled = voiceInputEnabled
            )
        }
    
    // Get search history as a Flow
    val searchHistoryFlow: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[SEARCH_HISTORY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        }
    
    // Update favorite categories
    suspend fun updateFavoriteCategories(categories: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_CATEGORIES] = categories.joinToString(",")
        }
    }
    
    // Add a category to favorites
    suspend fun addFavoriteCategory(category: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_CATEGORIES]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = (current + category).distinct()
            preferences[FAVORITE_CATEGORIES] = updated.joinToString(",")
        }
    }
    
    // Remove a category from favorites
    suspend fun removeFavoriteCategory(category: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_CATEGORIES]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = current.filter { it != category }
            preferences[FAVORITE_CATEGORIES] = updated.joinToString(",")
        }
    }
    
    // Update recent categories
    suspend fun updateRecentCategories(category: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[RECENT_CATEGORIES]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            // Remove if exists to avoid duplicates and add to the front
            val updated = listOf(category) + current.filter { it != category }.take(9)
            preferences[RECENT_CATEGORIES] = updated.joinToString(",")
        }
    }
    
    // Update search history
    suspend fun updateSearchHistory(query: String) {
        if (query.isBlank()) return
        
        context.dataStore.edit { preferences ->
            val current = preferences[SEARCH_HISTORY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            // Remove if exists to avoid duplicates and add to the front
            val updated = listOf(query) + current.filter { it != query }.take(9)
            preferences[SEARCH_HISTORY] = updated.joinToString(",")
        }
    }
    
    // Clear search history
    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY] = ""
        }
    }
    
    // Set show recents first preference
    suspend fun setShowRecentsFirst(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_RECENTS_FIRST] = enabled
        }
    }
    
    // Set voice input enabled preference
    suspend fun setVoiceInputEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VOICE_INPUT_ENABLED] = enabled
        }
    }
} 