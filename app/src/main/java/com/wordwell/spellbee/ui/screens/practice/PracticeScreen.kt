package com.wordwell.spellbee.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.wordwell.spellbee.data.model.Word
import com.wordwell.spellbee.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {
    val words = wordRepository.getAllWords()
    
    fun createPracticeWord(word: String): String {
        val indices = word.indices.toMutableList()
        val missingCount = word.length / 3
        val missingIndices = indices.shuffled().take(missingCount)
        return word.mapIndexed { index, char ->
            if (index in missingIndices) '_' else char
        }.joinToString("")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    navController: NavController,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    var currentWordIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(45) }
    var isTimerRunning by remember { mutableStateOf(false) }
    
    val words = viewModel.words
    val currentWord = words.getOrNull(currentWordIndex)
    val practiceWord = currentWord?.let { viewModel.createPracticeWord(it.word) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentWord != null && practiceWord != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            // TODO: Implement audio playback
                        }) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play pronunciation"
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer"
                            )
                            Text(
                                text = " $timeLeft s",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Text(
                        text = buildAnnotatedString {
                            practiceWord.forEach { char ->
                                withStyle(
                                    style = SpanStyle(
                                        color = if (char == '_') 
                                            MaterialTheme.colorScheme.primary
                                        else 
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    append(char.toString())
                                }
                            }
                        },
                        style = MaterialTheme.typography.headlineLarge
                    )

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Enter the missing letters") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (userInput.lowercase() == currentWord.word.lowercase()) {
                                // TODO: Show success message
                                if (currentWordIndex < words.size - 1) {
                                    currentWordIndex++
                                    userInput = ""
                                }
                            } else {
                                // TODO: Show error message
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check Answer")
                    }
                }
            }
        }
    }
} 