package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asString

data class CardUi(
    val code: String,
    val name: String,
    val subtitle: String,
    val affiliation: String,
    val faction: String,
    val color: String,
    val points: String?,
    val cost: String?,
    val health: String?,
    val type: String,
    val rarity: String,
    val diceRef: List<String>,
    val set: String,
    val quantity: Int = 0,
    val isBanned: Boolean = false,
    val isRestricted: Boolean = false,
)

fun Card.toCardUi() = CardUi(
    code = code,
    name = name,
    subtitle = subtitle ?: "",
    affiliation = affiliationName ?: "",
    faction = factionName,
    color = factionCode,
    points = points.asString()

    /*run {
        if (this.first == null && this.second == null) {
            null
        } else {
            if (this.second == null) {
                this.first.toString()
            } else {
                this.first.toString() + "/" + this.second.toString()
            }
        }
    }*/,
    cost = cost?.toString(),
    health = health.toString(),
    type = typeName,
    rarity = rarityName,
    diceRef = sides ?: emptyList(),
    set = setName
)


