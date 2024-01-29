package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DeckBaseEntity (
    @PrimaryKey val name: String,
    val creationDate: Long,
    val updateDate: Long,
    val formatCode: String,
    val formatName: String,
    val affiliationCode: String,

    val affiliationName: String,
)

@Entity(primaryKeys = ["deckName", "cardCode"])
data class SlotEntity(
    val deckName: String,
    val cardCode: String,
    val quantity: Int,
    val dice: Int,
    val dices: String?,
)

data class DeckEntity(
    @Embedded val deck: DeckBaseEntity,

    @Relation(
        parentColumn = "name",
        entityColumn = "deckName"
    )
    val slots:List<SlotEntity>
)