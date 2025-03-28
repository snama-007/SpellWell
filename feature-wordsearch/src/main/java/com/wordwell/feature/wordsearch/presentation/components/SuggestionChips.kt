package com.wordwell.feature.wordsearch.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Predefined set of light, visually distinct colors that work well with black text
private val suggestionColors = listOf(
    Color(0xFFFFE0B2), // Light Orange
    Color(0xFFC8E6C9), // Light Green
    Color(0xFFBBDEFB), // Light Blue
    Color(0xFFE1BEE7), // Light Purple
    Color(0xFFFFECB3), // Light Amber
    Color(0xFFB2DFDB), // Light Teal
    Color(0xFFFFCDD2), // Light Red
    Color(0xFFD1C4E9), // Light Deep Purple
    Color(0xFFFFCCBC), // Light Deep Orange
    Color(0xFFF8BBD0)  // Light Pink
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = suggestions.isNotEmpty(),
        enter = fadeIn(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 2 }
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 200)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis = 200),
            targetOffsetY = { it / 2 }
        )
    ) {
        LazyRow(
            modifier = modifier.padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(
                items = suggestions,
                key = { index, item -> item }
            ) { index, suggestion ->
                val backgroundColor = suggestionColors[index % suggestionColors.size]
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = backgroundColor,
                        labelColor = Color.Black
                    ),
                    /* DO NOT REMOVE THIS BORDER */
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    ),
                    /* DO NOT REMOVE THIS BORDER */
                    modifier =  Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SuggestionChipsPreview() {
    MaterialTheme {
        SuggestionChips(
            suggestions = listOf("red flowers", "animal words", "kitchen tools", "school items", "sports words"),
            onSuggestionClick = {}
        )
    }
} 