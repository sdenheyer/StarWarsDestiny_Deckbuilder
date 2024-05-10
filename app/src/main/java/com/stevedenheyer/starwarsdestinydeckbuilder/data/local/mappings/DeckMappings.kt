package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CharacterEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SlotEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
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

    battlefieldCardCode = if (deck.battlefieldCardCode == null) null else CardOrCode.HasCode(deck.battlefieldCardCode),
    plotCardCode = if (deck.plotCardCode == null) null else CardOrCode.HasCode(deck.plotCardCode),
    plotPoints = deck.plotPoints,
    isPlotElite = deck.isPlotElite,
    characters = characters.map { it.toDomain() },
    slots = slots.map { it.toDomain() }
)

fun Deck.toEntity() = DeckBaseEntity(
    name = name,
    creationDate = creationDate.time,
    updateDate = updateDate.time,
    formatCode = formatCode,
    formatName = formatName,
    affiliationCode = affiliationCode,
    affiliationName = affiliationName,

    battlefieldCardCode = battlefieldCardCode?.fetchCode(),
    plotCardCode = plotCardCode?.fetchCode(),
    isPlotElite = isPlotElite,
    plotPoints = plotPoints,
)

fun CharacterEntity.toDomain() = CharacterCard(
    cardOrCode = CardOrCode.HasCode(cardCode),
    points = points,
    quantity = quantity,
    isElite = isElite,
    dice = dice,
    dices = dices,
)

fun CharacterCard.toEntity(deckName: String) = CharacterEntity(
    deckName = deckName,
    cardCode = cardOrCode.fetchCode(),
    points = points,
    quantity = quantity,
    isElite = isElite,
    dice = dice,
    dices = dices,
)

fun SlotEntity.toDomain() = Slot(
    cardOrCode = CardOrCode.HasCode(cardCode),
    quantity = quantity,
    dice = dice,
    dices = dices,
)

fun Slot.toEntity(deckName: String) = SlotEntity(
    deckName = deckName,
    cardCode = cardOrCode.fetchCode(),
    quantity = quantity,
    dice = dice,
    dices = dices,
)