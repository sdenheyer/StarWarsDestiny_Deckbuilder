package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck

data class DeckUi(
    val name: String,
    val format: String,
    val affiliation: String,
    val cards: List<CardUi> = emptyList(),
)

fun Deck.toDeckUi() = DeckUi(
    name = name,
    format = formatName,
    affiliation = affiliationName,
)
