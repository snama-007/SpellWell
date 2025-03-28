package com.wordwell.feature.wordsearch.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordwell.feature.wordsearch.presentation.components.SearchBar
import com.wordwell.feature.wordsearch.presentation.components.SuggestionChips
import com.wordwell.feature.wordsearch.presentation.components.VoiceInputDialog
import com.wordwell.feature.wordsearch.voice.PermissionHelper

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFEB3B) // Default yellow background
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error messages as snackbars
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }
    
    // Show voice error messages as snackbars
    LaunchedEffect(state.voiceError) {
        state.voiceError?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }
    
    SearchContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
        backgroundColor = backgroundColor
    )
}

@Composable
fun SearchContent(
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFEB3B)
) {
    val context = LocalContext.current
    
    // Wrap everything in a Card with rounded corners
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            // Search Bar
            SearchBar(
                query = state.query,
                isWordSetMode = state.isWordSetMode,
                onQueryChange = { query -> 
                    onEvent(SearchEvent.QueryChanged(query)) 
                },
                onSearch = { 
                    onEvent(SearchEvent.SearchSubmitted) 
                },
                onVoiceInputClick = { 
                    // Check for permission first
                    onEvent(SearchEvent.CheckVoicePermission(context as Activity))
                },
                onModeToggle = { isWordSetMode -> 
                    onEvent(SearchEvent.ModeChanged(isWordSetMode)) 
                },
                onClearClick = { 
                    onEvent(SearchEvent.ClearSearch) 
                }
            )
            
            // Suggestion Chips
            SuggestionChips(
                suggestions = state.suggestions,
                onSuggestionClick = { suggestion ->
                    onEvent(SearchEvent.SuggestionSelected(suggestion))
                }
            )
            
            // Voice Input Dialog
            if (state.isVoiceInputActive) {
                VoiceInputDialog(
                    onDismiss = { 
                        onEvent(SearchEvent.VoiceInputToggled) 
                        onEvent(SearchEvent.StopVoiceListening)
                    },
                    onVoiceResult = { text ->
                        onEvent(SearchEvent.VoiceResultConfirmed(text))
                    },
                    isListening = state.isVoiceListening,
                    recognizedText = state.voiceRecognizedText,
                    onStartListening = {
                        onEvent(SearchEvent.StartVoiceListening)
                    },
                    onStopListening = {
                        onEvent(SearchEvent.StopVoiceListening)
                    }
                )
            }
        }
    }
} 