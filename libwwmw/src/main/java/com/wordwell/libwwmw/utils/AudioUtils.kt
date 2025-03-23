package com.wordwell.libwwmw.utils

/**
 * AudioUtils provides utility functions for handling audio-related operations
 */
internal object AudioUtils {
    
    /**
     * Determines the appropriate subdirectory for an audio file based on its name.
     * Rules:
     * - If audio begins with "bix", the subdirectory should be "bix"
     * - If audio begins with "gg", the subdirectory should be "gg"
     * - If audio begins with a number or punctuation, the subdirectory should be "number"
     * - Otherwise, the subdirectory is equal to the first letter of audio
     *
     * @param audio The audio file name
     * @return The appropriate subdirectory name
     */
    fun getAudioSubdirectory(audio: String): String {
        if (audio.isEmpty()) {
            return ""
        }
        
        return when {
            audio.startsWith("bix") -> "bix"
            audio.startsWith("gg") -> "gg"
            audio.first().isDigit() || !audio.first().isLetterOrDigit() -> "number"
            else -> audio.first().toString()
        }
    }
}