package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormat

fun FormatDTO.toDomain() = CardFormat(
    gameTypeName = name,
    gameTypeCode = code,
    includedSets = data.sets,
    banned = data.banned ?: emptyList(),
    restricted = data.restricted ?: emptyList(),
    balance = data.balance ?: emptyMap(),
    restrictedPairs = data.restrictedPairs ?: emptyMap(),
)