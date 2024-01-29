package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SlotEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import java.util.Date

fun DeckEntity.toDomain() = Deck(
    name = deck.name,
    creationDate = Date(deck.creationDate),
    updateDate = Date(deck.updateDate),
    formatCode = deck.formatCode,
    formatName = deck.formatName,
    affiliationCode = deck.affiliationCode,
    affiliationName = deck.affiliationName,
)

fun Deck.toEntity() = DeckBaseEntity(
    name = name,
    creationDate = creationDate.time,
    updateDate = updateDate.time,
    formatCode = formatCode,
    formatName = formatName,
    affiliationCode = affiliationCode,
    affiliationName = affiliationName,
)

fun SlotEntity.toDomain() = Slot(
    cardCode = cardCode,
    quantity = quantity,
    dice = dice,
    dices = dices,
)

fun Slot.toEntity(deckName: String) = SlotEntity(
    deckName = deckName,
    cardCode = cardCode,
    quantity = quantity,
    dice = dice,
    dices = dices,
)