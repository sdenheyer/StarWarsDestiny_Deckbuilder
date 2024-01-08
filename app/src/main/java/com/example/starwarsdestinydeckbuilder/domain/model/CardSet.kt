package com.example.starwarsdestinydeckbuilder.domain.model

import java.net.URL
import java.sql.Date

data class CardSet(
    val name: String,
    val code: String,
    val position: Int,
    val available: Date,
    val known: Int,
    val total: Int,
    val url: URL,
    )

data class CardSetList(
    val timestamp: Long,
    val expiry: Long,
    val cardSets: List<CardSet>
)

