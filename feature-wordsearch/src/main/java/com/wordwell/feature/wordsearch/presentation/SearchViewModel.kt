package com.wordwell.feature.wordsearch.presentation

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordwell.feature.wordsearch.data.MockSearchData
import com.wordwell.feature.wordsearch.data.PreferencesManager
import com.wordwell.feature.wordsearch.voice.PermissionHelper
import com.wordwell.feature.wordsearch.voice.VoiceRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    
    private var voiceRecognitionManager: VoiceRecognitionManager? = null
    
    init {
        // Load initial suggestions
        updateSuggestions("")
        
        // Set up debounced search
        setupDebouncedSearch()
        
        // Initialize voice recognition
        initVoiceRecognition()
        
        // Load preferences
        loadPreferences()
        
        // Load search history
        loadSearchHistory()
    }
    
    private fun loadPreferences() {
        preferencesManager.userPreferencesFlow
            .onEach { preferences ->
                _state.update { it.copy(userPreferences = preferences) }
                
                // Update suggestions based on preferences
                if (_state.value.query.isEmpty()) {
                    updateSuggestions("")
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadSearchHistory() {
        preferencesManager.searchHistoryFlow
            .onEach { history ->
                _state.update { it.copy(searchHistory = history) }
                
                // Update suggestions if appropriate
                if (_state.value.query.isEmpty() && 
                    _state.value.userPreferences.showRecentsFirst && 
                    history.isNotEmpty()
                ) {
                    updateSuggestions("")
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun initVoiceRecognition() {
        voiceRecognitionManager = VoiceRecognitionManager(context)
        
        // Collect voice recognition state
        viewModelScope.launch {
            voiceRecognitionManager?.state?.collect { voiceState ->
                _state.update { 
                    it.copy(
                        isVoiceListening = voiceState.isListening,
                        voiceRecognizedText = voiceState.recognizedText,
                        voiceError = voiceState.error
                    )
                }
            }
        }
    }
    
    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            _queryFlow
                .debounce(300L) // 300ms debounce
                .collect { query ->
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    }
                }
        }
    }
    
    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _state.update { it.copy(query = event.query) }
                _queryFlow.value = event.query
                updateSuggestions(event.query)
            }
            
            is SearchEvent.ModeChanged -> {
                _state.update { it.copy(isWordSetMode = event.isWordSetMode) }
                // Re-run search with new mode if query exists
                if (_state.value.query.isNotEmpty()) {
                    performSearch(_state.value.query)
                }
            }
            
            is SearchEvent.SuggestionSelected -> {
                _state.update { it.copy(query = event.suggestion) }
                _queryFlow.value = event.suggestion
                performSearch(event.suggestion)
                // Save to search history
                addToSearchHistory(event.suggestion)
            }
            
            SearchEvent.VoiceInputToggled -> {
                _state.update { it.copy(isVoiceInputActive = !it.isVoiceInputActive) }
            }
            
            SearchEvent.StartVoiceListening -> {
                voiceRecognitionManager?.startListening()
            }
            
            SearchEvent.StopVoiceListening -> {
                voiceRecognitionManager?.stopListening()
            }
            
            is SearchEvent.VoiceResultConfirmed -> {
                val query = event.text
                _state.update { 
                    it.copy(
                        query = query,
                        isVoiceInputActive = false
                    ) 
                }
                _queryFlow.value = query
                performSearch(query)
                // Save to search history
                addToSearchHistory(query)
                // Clear voice recognition text
                voiceRecognitionManager?.clearRecognizedText()
            }
            
            SearchEvent.SearchSubmitted -> {
                performSearch(_state.value.query)
                // Save to search history
                addToSearchHistory(_state.value.query)
            }
            
            SearchEvent.ClearSearch -> {
                _state.update { 
                    it.copy(query = "", searchResults = emptyList()) 
                }
                updateSuggestions("")
            }
            
            is SearchEvent.CheckVoicePermission -> {
                if (!PermissionHelper.hasAudioPermission(context)) {
                    PermissionHelper.requestAudioPermission(event.activity)
                } else {
                    _state.update { it.copy(isVoiceInputActive = true) }
                }
            }
            
            is SearchEvent.UpdateUserPreference -> {
                updateUserPreference(event.preference)
            }
        }
    }
    
    private fun updateUserPreference(preference: UserPreferenceUpdate) {
        viewModelScope.launch {
            when (preference) {
                is UserPreferenceUpdate.AddFavoriteCategory -> {
                    preferencesManager.addFavoriteCategory(preference.category)
                }
                is UserPreferenceUpdate.RemoveFavoriteCategory -> {
                    preferencesManager.removeFavoriteCategory(preference.category)
                }
                is UserPreferenceUpdate.SetShowRecentsFirst -> {
                    preferencesManager.setShowRecentsFirst(preference.enabled)
                }
                is UserPreferenceUpdate.SetVoiceInputEnabled -> {
                    preferencesManager.setVoiceInputEnabled(preference.enabled)
                }
            }
        }
    }
    
    private fun performSearch(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Extract possible category from query
            val category = extractCategory(query)
            if (category != null) {
                preferencesManager.updateRecentCategories(category)
            }
            
            // Simulate network delay
            delay(500)
            
            try {
                // Get mock results based on query and current mode
                val results = MockSearchData.getSearchResults(
                    query = query,
                    isWordSetMode = _state.value.isWordSetMode
                )
                
                _state.update { 
                    it.copy(
                        searchResults = results,
                        isLoading = false,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to perform search: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    private fun extractCategory(query: String): String? {
        // Simple logic to extract category from a query
        // For example "animal words" -> "animals"
        val categoryMap = mapOf(
            "animal" to "animals",
            "flower" to "nature",
            "vegetable" to "food",
            "fruit" to "food",
            "superhero" to "fantasy",
            "space" to "science",
            "kitchen" to "house",
            "weather" to "nature",
            "dinosaur" to "science"
        )
        
        return categoryMap.entries.firstOrNull { query.contains(it.key, ignoreCase = true) }?.value
    }
    
    private fun updateSuggestions(query: String) {
        val userPrefs = _state.value.userPreferences
        val recentSearches = _state.value.searchHistory.take(3)
        
        val suggestions = when {
            // When query is empty, show recent searches or favorite categories first if appropriate
            query.isEmpty() && userPrefs.showRecentsFirst && recentSearches.isNotEmpty() -> {
                // Show recent searches first, then suggestions that aren't already in recents
                recentSearches + createPersonalizedSuggestions(query).filter { it !in recentSearches }.take(5)
            }
            
            // Otherwise use personalized suggestions
            else -> createPersonalizedSuggestions(query)
        }
        
        _state.update { it.copy(suggestions = suggestions) }
    }
    
    private fun createPersonalizedSuggestions(query: String): List<String> {
        val userPrefs = _state.value.userPreferences
        
        // Start with default suggestions
        val allSuggestions = MockSearchData.defaultSuggestions.toMutableList()
        
        // Add suggestions from favorite categories if available
        userPrefs.favoriteCategories.forEach { category ->
            MockSearchData.categorizedSuggestions[category]?.let { categorySuggestions ->
                allSuggestions.addAll(categorySuggestions)
            }
        }
        
        // Add suggestions from recent categories if available
        userPrefs.recentCategories.forEach { category ->
            MockSearchData.categorizedSuggestions[category]?.let { categorySuggestions ->
                // Add one suggestion from each recent category for variety
                categorySuggestions.firstOrNull()?.let { suggestion ->
                    allSuggestions.add(suggestion)
                }
            }
        }
        
        // Filter by query if not empty
        val filteredSuggestions = if (query.isNotBlank()) {
            allSuggestions.filter { it.contains(query, ignoreCase = true) }
        } else {
            allSuggestions
        }
        
        // Return distinct, limited suggestions
        return filteredSuggestions.distinct().take(8)
    }
    
    private fun addToSearchHistory(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            preferencesManager.updateSearchHistory(query)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        voiceRecognitionManager?.destroy()
    }
} 