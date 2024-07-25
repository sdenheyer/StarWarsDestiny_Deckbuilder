package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
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

    val battlefieldCardCode: String?,
    val plotCardCode: String?,
    val isPlotElite: Boolean = false,
    val plotPoints: Int,
)

@Entity(primaryKeys = ["deckName", "cardCode"])
data class CharacterEntity(
    val deckName: String,
    val cardCode: String,
    val points: Int,
    val isElite: Boolean,
    val quantity: Int,
    val dice: Int,
    val dices: String?,

    @ColumnInfo(defaultValue = "0")val setAside: Boolean,
)

@Entity(primaryKeys = ["deckName", "cardCode"])
data class SlotEntity(
    val deckName: String,
    val cardCode: String,
    val quantity: Int,
    val dice: Int,
    val dices: String?,

    @ColumnInfo(defaultValue = "0")val setAside: Boolean,
)

data class DeckEntity(
    @Embedded val deck: DeckBaseEntity,

    @Relation(
        parentColumn = "name",
        entityColumn = "deckName",
    )
    val characters:List<CharacterEntity>,

    @Relation(
        parentColumn = "name",
        entityColumn = "deckName",
    )
    val slots:List<SlotEntity>
)