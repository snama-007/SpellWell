package com.wordwell.spellbee.data.model

data class Word(
    val id: Int,
    val word: String,
    val syllables: List<String>,
    val phonetics: String,
    val audioUrl: String,
    val grade: Int,
    val difficulty: Int
) 