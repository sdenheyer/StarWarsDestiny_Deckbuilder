package com.example.starwarsdestinydeckbuilder.domain.model

data class CardFormat(
    val gameTypeName: String,
    val gameTypeCode: String,
    val includedSets: List<String>,
    val banned: List<String>,
    val restricted: List<String>,
    val balance: Map<String, String>,
    val restrictedPairs: Map<String, Array<String>>?
)
