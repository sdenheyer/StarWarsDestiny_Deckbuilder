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

    val timestamp: Long,
    )
