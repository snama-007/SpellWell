package com.wordwell.feature.wordsearch.presentation

import android.app.Activity

/**
 * Represents events that can occur in the search feature
 */
sealed interface SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data class ModeChanged(val isWordSetMode: Boolean) : SearchEvent
    data class SuggestionSelected(val suggestion: String) : SearchEvent
    object VoiceInputToggled : SearchEvent
    object StartVoiceListening : SearchEvent
    object StopVoiceListening : SearchEvent
    data class VoiceResultConfirmed(val text: String) : SearchEvent
    data class CheckVoicePermission(val activity: Activity) : SearchEvent
    object SearchSubmitted : SearchEvent
    object ClearSearch : SearchEvent
    data class UpdateUserPreference(val preference: UserPreferenceUpdate) : SearchEvent
}

/**
 * Types of user preference updates
 */
sealed interface UserPreferenceUpdate {
    data class AddFavoriteCategory(val category: String) : UserPreferenceUpdate
    data class RemoveFavoriteCategory(val category: String) : UserPreferenceUpdate
    data class SetShowRecentsFirst(val enabled: Boolean) : UserPreferenceUpdate
    data class SetVoiceInputEnabled(val enabled: Boolean) : UserPreferenceUpdate
} 