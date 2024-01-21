package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

data class CardFormat(
    val gameTypeName: String,
    val gameTypeCode: String,
    val includedSets: List<String>,
    val banned: List<String>,
    val restricted: List<String>,
    val balance: Map<String, String>,
    val restrictedPairs: Map<String, Array<String>>,
)

data class CardFormatList(
    val timestamp: Long,
    val expiry: Long,
    val cardFormats: List<CardFormat>
)
