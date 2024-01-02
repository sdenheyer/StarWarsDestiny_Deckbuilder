package com.example.starwarsdestinydeckbuilder.data.remote.mappings

import com.example.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat

fun FormatDTO.toDomain() = CardFormat(
    gameTypeName = name,
    gameTypeCode = code,
    includedSets = data.sets,
    banned = data.banned,
    restricted = data.restricted,
    balance = data.balance,
    restrictedPairs = data.restrictedPairs,
)