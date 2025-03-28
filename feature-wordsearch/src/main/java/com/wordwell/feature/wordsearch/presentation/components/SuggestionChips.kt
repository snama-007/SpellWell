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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
            modifier = modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            itemsIndexed(
                items = suggestions,
                key = { index, item -> item }
            ) { index, suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border =SuggestionChipDefaults.suggestionChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    ),
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
            suggestions = listOf("red flowers", "animal words", "kitchen tools"),
            onSuggestionClick = {}
        )
    }
} 