package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Subtype
import java.net.URL
import java.util.Date

fun CardEntity.toDomain() = Card(
    sides = if (card.side1 == null) { null } else { listOf(card.side1!!, card.side2!!, card.side3!!, card.side4!!, card.side5!!, card.side6!!) },
    setCode = card.setCode,
    setName = card.setName,
    typeCode = card.typeCode,
    typeName = card.typeName,
    factionCode = card.factionCode,
    factionName = card.factionName,
    affiliationCode = card.affiliationCode,
    affiliationName = card.affiliationName,
    rarityCode = card.rarityCode,
    rarityName = card.rarityName,
    subtypes = subTypes.map { Subtype(code = it.subTypeCode, name = it.name) },
    position = card.position,
    code = card.code,
    ttsCardID = card.ttsCardID,
    name = card.name,
    subtitle = card.subtitle,
    cost = card.cost,
    health = card.health,
    points = Pair(first = card.points1, second = card.points2),
    text = card.text,
    deckLimit = card.deckLimit,
    flavor = card.flavor,
    illustrator = card.illustrator,
    isUnique = card.isUnique,
    hasDie = card.hasDie,
    hasErrata = card.hasErrata,
    flipCard = card.flipCard,
    url = URL(card.url),
    imageSrc = URL(card.imageSrc),
    label = card.label,
    cp = card.cp,
    reprints = reprints.map { CardOrCode.hasCode(it.cardCode) },
    parallelDiceOf = parellelDiceOf.map { CardOrCode.hasCode(it.cardCode) },

    timestamp = card.timestamp,
    expiry = card.expiry,
)

fun Card.toEntity() = CardBaseEntity(
    side1 = sides?.get(0),
    side2 = sides?.get(1),
    side3 = sides?.get(2),
    side4 = sides?.get(3),
    side5 = sides?.get(4),
    side6 = sides?.get(5),
    setCode = setCode,
    setName = setName,
    typeCode = typeCode,
    typeName = typeName,
    factionCode = factionCode,

    factionName = factionName,
    affiliationCode = affiliationCode,
    affiliationName = affiliationName,
    rarityCode = rarityCode,
    rarityName = rarityName,
    position = position,
    code = code,
    ttsCardID = ttsCardID,
    name = name,
    subtitle = subtitle,
    cost = cost,
    health = health,
    points1 = points.first,
    points2 = points.second,
    text = text,
    deckLimit = deckLimit,
    flavor = flavor,
    illustrator = illustrator,
    isUnique = isUnique,
    hasDie = hasDie,
    hasErrata = hasErrata,
    flipCard = flipCard,
    url = url.toExternalForm(),
    imageSrc = imageSrc.toExternalForm(),
    label = label,
    cp = cp,

    timestamp = Date().time,
    expiry = expiry
)