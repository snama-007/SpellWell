package com.wordwell.libwwmw.domain.models

data class WordItem(
    val def: List<Def>,
    val fl: String,
    val history: History,
    val hom: Int,
    val hwi: Hwi,
    val ins: List<In>,
    val meta: Meta,
    val shortdef: List<String>
)

data class Def(
    val sseq: List<List<List<Any>>>
)

data class History(
    val pl: String,
    val pt: List<List<String>>
)

data class Hwi(
    val hw: String,
    val prs: List<Pr>
)

data class In(
    val inf: String
)

data class Meta(
    val id: String,
    val offensive: Boolean,
    val section: String,
    val sort: String,
    val src: String,
    val stems: List<String>,
    val uuid: String
)

data class Pr(
    val mw: String,
    val sound: Sound
)

data class Sound(
    val audio: String
)