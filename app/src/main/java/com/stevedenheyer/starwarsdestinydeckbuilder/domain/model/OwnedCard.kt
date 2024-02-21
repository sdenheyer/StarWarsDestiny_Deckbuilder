package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CodeQuantity

data class OwnedCard(
    val card: CardOrCode,
    val quantity: Int,
)
