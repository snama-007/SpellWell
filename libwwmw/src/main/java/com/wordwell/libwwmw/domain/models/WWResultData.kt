package com.wordwell.libwwmw.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class WWResultData : Parcelable {
    @Parcelize
    data class WordSetResult(
        val wordSet: List<WordSet> = emptyList()
    ) : Parcelable, WWResultData()

    @Parcelize
    data class WordResult(
        val setName: String,
        val words: List<Word> = emptyList()
    ) : Parcelable, WWResultData()
}