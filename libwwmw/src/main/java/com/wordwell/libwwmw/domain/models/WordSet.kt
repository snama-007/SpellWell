package com.wordwell.libwwmw.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordSet(
    val id: String,
    val name: String,
    val numberOfWords: Int = 0
): Parcelable

