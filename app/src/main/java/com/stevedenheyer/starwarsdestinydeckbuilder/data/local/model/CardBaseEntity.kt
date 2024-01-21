package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class CardBaseEntity(
    val side1: String?,
    val side2: String?,
    val side3: String?,
    val side4: String?,
    val side5: String?,
    val side6: String?,
    val setCode: String,
    val setName: String,
    val typeCode: String,
    val typeName: String,
    val factionCode: String,
    val factionName: String,
    val affiliationCode: String?,
    val affiliationName: String?,
    val rarityCode: Char,
    val rarityName: String,
    val position: Int,
    @PrimaryKey val code: String,
    val ttsCardID: Int,
    val name: String,
    val subtitle: String?,
    val cost: Int?,
    val health: Int?,
    val points: String?,
    val text: String?,
    val deckLimit: Int,
    val flavor: String?,
    val illustrator: String?,
    val isUnique: Boolean,
    val hasDie: Boolean,
    val hasErrata: Boolean,
    val flipCard: Boolean,
    val url: String,
    val imageSrc: String,
    val label: String,
    val cp: Int,

    val timestamp: Long,
    val expiry: Long,
)

@Entity
data class SubTypeEntity(
    @PrimaryKey val subTypeCode: String,
    val name: String
)

@Entity(primaryKeys = ["code", "subTypeCode"])
data class CardSubtypeCrossRef(
    val code: String,
    val subTypeCode: String
)

@Entity(primaryKeys = ["code", "cardCode"])
data class CardReprintsCrossRef(
    val code: String,
    val cardCode: String
)

@Entity(primaryKeys = ["code", "cardCode"])
data class CardParellelDiceCrossRef(
    val code: String,
    val cardCode: String
)

data class CardEntity(
    @Embedded val card: CardBaseEntity,

    @Relation(
        parentColumn = "code",
        entityColumn = "subTypeCode",
        associateBy = Junction(CardSubtypeCrossRef::class)
    )
    val subTypes: List<SubTypeEntity>,

    @Relation(
        parentColumn = "code",
        entityColumn = "cardCode",
        associateBy = Junction(CardReprintsCrossRef::class)
    )
    val reprints: List<CardCode>,

    @Relation(
        parentColumn = "code",
        entityColumn = "cardCode",
        associateBy = Junction(CardParellelDiceCrossRef::class)
    )
    val parellelDiceOf: List<CardCode>,
)


/*
Kinda think this should be saved in entirely separate record to mirror DTO and constructed during composition

data class CardFormat(
    @Embedded val card: CardEntity,
    @Relation(
        parentColumn = "code",
        entityColumn = "cardCode"
    )
    val formats: List<FormatEntity>
)
@Entity
data class FormatEntity(
    @PrimaryKey val id: Long,
    val cardCode: String,
    val gameType: String,
    val legality: String,
    val balance: String,
)


*/
