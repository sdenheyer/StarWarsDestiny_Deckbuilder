package com.example.starwarsdestinydeckbuilder.data.local.mappings

import com.example.starwarsdestinydeckbuilder.data.local.model.Balance
import com.example.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import java.util.Date

fun CardFormat.toEntity() = FormatEntity(
    FormatBaseEntity(
        gameTypeName = gameTypeName,
        gameTypeCode = gameTypeCode,
        timeStamp = Date().time,
    ),
    includedSets = includedSets.map { SetCode(it) },
    restricted = restricted.map { CardCode(it) },
    banned = banned.map { CardCode(it) },
    balance = balance.map { Balance(it.key, it.value) },
   // restrictedPairs = null,
)

fun FormatEntity.toDomain() = CardFormat(
    gameTypeName = cardFormat.gameTypeName,
    gameTypeCode = cardFormat.gameTypeCode,
    includedSets = includedSets.map { it.setCode },
    restricted = restricted.map { it.cardCode },
    banned = banned.map { it.cardCode },
    balance = balance.associate { Pair(it.cardCode, it.balance) },
    restrictedPairs = null,
)