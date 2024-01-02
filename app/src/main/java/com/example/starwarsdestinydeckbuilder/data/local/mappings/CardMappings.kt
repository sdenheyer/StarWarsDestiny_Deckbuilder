package com.example.starwarsdestinydeckbuilder.data.local.mappings

import com.example.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import java.net.URL
import java.util.Date

fun CardBaseEntity.toDomain() = Card(
    sides = if (side1 == null) { null } else { listOf(side1!!, side2!!, side3!!, side4!!, side5!!, side6!!) },
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
    subtypes = emptyList(),
    position = position,
    code = code,
    ttsCardID = ttsCardID,
    name = name,
    subtitle = subtitle,
    cost = cost,
    health = health,
    points = points,
    text = text,
    deckLimit = deckLimit,
    flavor = flavor,
    illustrator = illustrator,
    isUnique = isUnique,
    hasDie = hasDie,
    hasErrata = hasErrata,
    flipCard = flipCard,
    url = URL(url),
    imageSrc = URL(imageSrc),
    label = label,
    cp = cp,
    reprints = emptyList(),
    parallelDiceOf = emptyList(),

    timestamp = timestamp
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
// val subtypes = List<Subtype>?,  TODO: Set this up as an Entity
    position = position,
    code = code,
    ttsCardID = ttsCardID,
    name = name,
    subtitle = subtitle,
    cost = cost,
    health = health,
    points = points,
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

    timestamp = Date().time
// val reprints: List<Int>?,  Setup as One-to-Many
// val parallelDiceOf: List<Int>?, Setup as One-to-Many

    /*val legalityStandard: Int,   //TODO:  Set these up as entities
    val legalityTrilogy: Int,
    val legalityInfinite: Int,
    val legalityARHStandard: Int,*/

   // balance = balance
)