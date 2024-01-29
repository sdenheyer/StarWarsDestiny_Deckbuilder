package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

import java.util.Date

data class Deck(
    val name: String,
    val creationDate: Date,
    val updateDate: Date,
    val description: String = "",
    val formatCode: String,
    val formatName: String,
    val affiliationCode: String,
    val affiliationName: String,
    val slots: List<Slot> = emptyList()
)

data class Slot(
    val cardCode: String,
    val quantity: Int,
    val dice: Int,
    val dices: String?
)
