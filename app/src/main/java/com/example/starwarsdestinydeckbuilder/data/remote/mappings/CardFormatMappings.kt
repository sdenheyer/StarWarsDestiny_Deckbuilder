package com.example.starwarsdestinydeckbuilder.data.remote.mappings

import com.example.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import java.util.Date

fun FormatDTO.toDomain() = CardFormat(
    gameTypeName = name,
    gameTypeCode = code,
    includedSets = data.sets,
    banned = data.banned ?: emptyList(),
    restricted = data.restricted ?: emptyList(),
    balance = data.balance ?: emptyMap(),
    restrictedPairs = data.restrictedPairs ?: emptyMap(),
    timestamp = Date().time,
)