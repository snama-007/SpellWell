package com.wordwell.feature.wordsearch.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onVoiceResult: (String) -> Unit,
    isListening: Boolean,
    recognizedText: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dialog header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Search",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Microphone with animation
                MicrophoneButton(
                    isListening = isListening,
                    onClick = { 
                        if (isListening) onStopListening() else onStartListening() 
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Instructions
                Text(
                    text = if (isListening) "Listening..." else "Tap microphone to start",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (recognizedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Recognized text
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = recognizedText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Use button
                    Button(
                        onClick = { onVoiceResult(recognizedText) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use This Text")
                    }
                }
            }
        }
    }
}

@Composable
fun MicrophoneButton(
    isListening: Boolean,
    onClick: () -> Unit
) {
    // Animation for microphone
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.2f else 1f,
        animationSpec = if (isListening) {
            infiniteRepeatable(
                animation = tween(500)
            )
        } else {
            tween(300)
        },
        label = "MicScale"
    )
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                color = if (isListening) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.primaryContainer
            )
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                tint = if (isListening) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }
    }
} 