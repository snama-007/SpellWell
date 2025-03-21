package com.wordwell.spellbee.data.repository

import com.wordwell.spellbee.data.model.Word
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor() {
    private val mockWords = listOf(
        Word(
            id = 1,
            word = "beautiful",
            syllables = listOf("beau", "ti", "ful"),
            phonetics = "ˈbjuːtɪfʊl",
            audioUrl = "beautiful.mp3",
            grade = 3,
            difficulty = 2
        ),
        Word(
            id = 2,
            word = "elephant",
            syllables = listOf("el", "e", "phant"),
            phonetics = "ˈɛlɪfənt",
            audioUrl = "elephant.mp3",
            grade = 2,
            difficulty = 1
        ),
        Word(
            id = 3,
            word = "extraordinary",
            syllables = listOf("ex", "traor", "di", "nar", "y"),
            phonetics = "ɪkˈstrɔːdɪnəri",
            audioUrl = "extraordinary.mp3",
            grade = 5,
            difficulty = 3
        ),
        Word(
            id = 4,
            word = "butterfly",
            syllables = listOf("but", "ter", "fly"),
            phonetics = "ˈbʌtərflaɪ",
            audioUrl = "butterfly.mp3",
            grade = 2,
            difficulty = 1
        ),
        Word(
            id = 5,
            word = "knowledge",
            syllables = listOf("knowl", "edge"),
            phonetics = "ˈnɒlɪdʒ",
            audioUrl = "knowledge.mp3",
            grade = 4,
            difficulty = 2
        )
    )

    fun getAllWords(): List<Word> = mockWords

    fun getWordsByGrade(grade: Int): List<Word> =
        mockWords.filter { it.grade <= grade }

    fun getWordById(id: Int): Word? =
        mockWords.find { it.id == id }
} 