package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CodeQuantity
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard

fun CodeQuantity.toDomain() = OwnedCard(
    card = CardOrCode.HasCode(cardCode),
    quantity = quantity
)

fun OwnedCard.toEntity() = CodeQuantity(
    cardCode = card.fetchCode(),
    quantity = quantity
)