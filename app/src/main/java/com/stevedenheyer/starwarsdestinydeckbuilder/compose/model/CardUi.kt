package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card

data class CardUi(
    val code: String,
    val name: String,
    val isUnique: Boolean,
    val subtitle: String,
    val affiliation: String,
    val faction: String,
    val color: String,
    val points: Pair<Int?, Int?>,
    val cost: Int?,
    val health: Int?,
    val type: String,
    val rarity: String,
    val diceRef: List<String>,
    val set: String,
    val position: Int,

    val quantity: Int = 0,

    val isElite: Boolean = false,
    val isBanned: Boolean = false,
    val isRestricted: Boolean = false,

    val uniqueWarning: Boolean = false,
    val factionMismatchWarning: Boolean = false,
    val affiliationMismatchWarning: Boolean = false,
)

fun Card.toCardUi() = CardUi(
    code = code,
    name = name,
    isUnique = isUnique,
    subtitle = subtitle ?: "",
    affiliation = affiliationName ?: "",
    faction = factionName,
    color = factionCode,
    points = points,
    cost = cost,
    health = health,
    type = typeName,
    rarity = rarityName,
    diceRef = sides ?: emptyList(),
    set = setName,
    position = position,
    quantity = quantity
)


