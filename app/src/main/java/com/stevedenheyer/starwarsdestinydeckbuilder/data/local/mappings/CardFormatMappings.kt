package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.Balance
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormat

fun CardFormat.toEntity() = FormatEntity(
    FormatBaseEntity(
        gameTypeName = gameTypeName,
        gameTypeCode = gameTypeCode,
    ),
    includedSets = includedSets.map { SetCode(it) },
    restricted = restricted.map { CardCode(it) },
    banned = banned.map { CardCode(it) },
    balance = balance.map { Balance(it.key, it.value) },
)

fun FormatEntity.toDomain() = CardFormat(
    gameTypeName = cardFormat.gameTypeName,
    gameTypeCode = cardFormat.gameTypeCode,
    includedSets = includedSets.map { it.setCode },
    restricted = restricted.map { it.cardCode },
    banned = banned.map { it.cardCode },
    balance = balance.associate { Pair(it.cardCode, it.balance) },
    restrictedPairs = emptyMap(),
)