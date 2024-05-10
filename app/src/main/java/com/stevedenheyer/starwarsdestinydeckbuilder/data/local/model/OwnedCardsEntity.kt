package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class OwnedCardsBaseEntity(
    @PrimaryKey val id: Int = 0,    //Yes, we only want one of these
    )

@Entity
data class CodeQuantity(
    val ownerId: Int = 0,
    @PrimaryKey val cardCode: String,
    val quantity: Int,
)

data class OwnedCardsEntity(
    @Embedded val ownedCardsBaseEntity: OwnedCardsBaseEntity,

    @Relation(parentColumn = "id",
                entityColumn = "ownerId",)

    val codes: List<CodeQuantity>,
)