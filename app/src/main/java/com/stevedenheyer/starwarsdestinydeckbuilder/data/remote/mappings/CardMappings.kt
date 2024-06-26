package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asIntPair
import java.net.URL
import java.util.Date

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
    points = points.asIntPair(),
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
    reprints = reprints?.map { CardOrCode.HasCode(it) } ?: emptyList(),
    parallelDiceOf = parallel_dice_of?.map { CardOrCode.HasCode(it) } ?: emptyList(),

    timestamp = Date().time,
)



