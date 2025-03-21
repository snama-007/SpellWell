package com.wordwell.spellbee.ui.screens.learn

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wordwell.spellbee.data.model.Word
import com.wordwell.spellbee.data.repository.WordRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    navController: NavController,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val words by viewModel.words.collectAsState()
    val currentWordIndex by viewModel.currentWordIndex.collectAsState()
    val currentWord = viewModel.getCurrentWord()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentWord != null) {
            WordCard(
                word = currentWord,
                onNext = { viewModel.nextWord() },
                onPrevious = { viewModel.previousWord() }
            )
        } else {
            Text(
                text = "No words available",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun WordCard(
    word: Word,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = word.syllables.joinToString(" · "),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = word.phonetics,
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = {
                // TODO: Implement audio playback
            }) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Play pronunciation"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPrevious) {
                    Text("Previous")
                }
                Button(onClick = onNext) {
                    Text("Next")
                }
            }
        }
    }
} 