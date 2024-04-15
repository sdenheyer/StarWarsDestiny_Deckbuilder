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

    val battlefieldCardCode: CardOrCode? = null,
    val plotCardCode: CardOrCode? = null,
    val isPlotElite: Boolean = false,
    val plotPoints: Int = 0,

    val characters: List<CharacterCard> = emptyList(),

    val slots: List<Slot> = emptyList()
)

data class CharacterCard(
    val cardOrCode: CardOrCode,
    val points: Int,
    val quantity: Int,
    val isElite: Boolean,
    val dice: Int,
    val dices: String?,
) {

}

data class Slot(
    val cardOrCode: CardOrCode,
    val quantity: Int,
    val dice: Int,
    val dices: String?
)
