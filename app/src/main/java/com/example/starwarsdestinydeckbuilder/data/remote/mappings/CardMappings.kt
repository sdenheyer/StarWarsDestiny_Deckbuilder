package com.example.starwarsdestinydeckbuilder.data.remote.mappings

import com.example.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.LEGALITY_ALLOWED
import java.net.URL

fun CardDTO.toDomain() = Card(
    sides = sides,
    setCode = set_code,
    setName = set_name,
    typeCode = type_code,
    typeName = type_name,
    factionCode = faction_code,
    factionName = faction_name,
    affiliationCode = affiliation_code,
    affiliationName = affiliation_name,
    rarityCode = rarity_code,
    rarityName = rarity_name,
    subtypes = subtypes,
    position = position,
    code = code,
    ttsCardID = ttscardid,
    name = name,
    subtitle = subtitle,
    cost = cost,
    health = health,
    points = points,
    text = text,
    deckLimit = deck_limit,
    flavor = flavor,
    illustrator = illustrator,
    isUnique = is_unique,
    hasDie = has_die,
    hasErrata = has_errata,
    flipCard = flip_card,
    url = URL(url),
    imageSrc = URL(imagesrc),
    label = label,
    cp = cp,
    reprints = reprints,
    parallelDiceOf = paralleldiceof,

    legalityStandard = LEGALITY_ALLOWED,
    legalityTrilogy = LEGALITY_ALLOWED,
    legalityInfinite = LEGALITY_ALLOWED,
    legalityARHStandard = LEGALITY_ALLOWED,

    balance = null,

    timestamp = 0
)



