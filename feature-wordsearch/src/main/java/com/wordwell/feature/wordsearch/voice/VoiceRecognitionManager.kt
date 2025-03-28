package com.wordwell.feature.wordsearch.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages voice recognition functionality
 */
class VoiceRecognitionManager(
    private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    
    private val _state = MutableStateFlow(VoiceRecognitionState())
    val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()
    
    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            initSpeechRecognizer()
        } else {
            _state.update { it.copy(error = "Speech recognition not available on this device") }
        }
    }
    
    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(createRecognitionListener())
    }
    
    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _state.update { it.copy(isListening = true, error = null) }
        }

        override fun onBeginningOfSpeech() {
            // No action needed
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Could be used to show voice amplitude visualization
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // No action needed
        }

        override fun onEndOfSpeech() {
            _state.update { it.copy(isListening = false) }
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            
            _state.update { 
                it.copy(
                    isListening = false,
                    error = errorMessage
                ) 
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0] // Get the most likely result
                _state.update { 
                    it.copy(
                        recognizedText = recognizedText,
                        isListening = false,
                        error = null
                    )
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val partialText = matches[0]
                _state.update { it.copy(partialText = partialText) }
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // No action needed
        }
    }
    
    fun startListening() {
        if (_state.value.isListening) return
        
        _state.update { it.copy(recognizedText = "", partialText = "", error = null) }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _state.update { 
                it.copy(
                    error = "Failed to start speech recognition: ${e.message}", 
                    isListening = false
                ) 
            }
        }
    }
    
    fun stopListening() {
        if (!_state.value.isListening) return
        
        try {
            speechRecognizer?.stopListening()
            _state.update { it.copy(isListening = false) }
        } catch (e: Exception) {
            _state.update { 
                it.copy(
                    error = "Failed to stop speech recognition: ${e.message}", 
                    isListening = false
                ) 
            }
        }
    }
    
    fun clearRecognizedText() {
        _state.update { it.copy(recognizedText = "", partialText = "") }
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

/**
 * Represents the state of voice recognition
 */
data class VoiceRecognitionState(
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val partialText: String = "",
    val error: String? = null
) 