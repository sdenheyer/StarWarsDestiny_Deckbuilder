package com.example.starwarsdestinydeckbuilder.data.remote.model

data class FormatDTO(
    val name: String,
    val code: String,
    val data: Data,
)

data class Data(
    val balance: Map<String, String>?,
    val banned: List<String>?,
    val errata: List<String>?,
    val restrictedPairs: Map<String, Array<String>>?,
    val restricted: List<String>?,
    val sets: List<String>
)
