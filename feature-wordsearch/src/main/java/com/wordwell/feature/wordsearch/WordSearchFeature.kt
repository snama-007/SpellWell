package com.wordwell.feature.wordsearch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordwell.feature.wordsearch.presentation.SearchScreen
import com.wordwell.feature.wordsearch.presentation.SearchViewModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feature module entry point for the WordSearch functionality
 */
@Singleton
class WordSearchFeature @Inject constructor() {

    /**
     * Returns the main search UI composable
     * 
     * @param modifier The modifier to be applied to the composable
     * @param backgroundColor Background color for the search UI card
     * @return Search UI composable
     */
    @Composable
    fun SearchUI(
        modifier: Modifier = Modifier,
        backgroundColor: Color = Color(0xFFFFEB3B) // Default yellow background
    ) {
        val viewModel: SearchViewModel = hiltViewModel()
        SearchScreen(
            viewModel = viewModel, 
            modifier = modifier,
            backgroundColor = backgroundColor
        )
    }
} 