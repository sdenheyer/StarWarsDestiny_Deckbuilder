package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class FormatBaseEntity(
    val gameTypeName: String,
    @PrimaryKey val gameTypeCode: String,
)

@Entity
data class SetCode(
    @PrimaryKey val setCode: String,
)

@Entity(primaryKeys = ["gameTypeCode", "setCode"])
data class FormatSetCrossref(
    val gameTypeCode: String,
    val setCode: String
)

@Entity(primaryKeys = ["gameTypeCode", "cardCode"])
data class FormatBannedCrossref(
    val gameTypeCode: String,
    val cardCode: String,
)

@Entity(primaryKeys = ["gameTypeCode", "cardCode"])
data class FormatRestrictedCrossref(
    val gameTypeCode: String,
    val cardCode: String,
)

@Entity(primaryKeys = ["balance", "cardCode"])
data class Balance(
    val balance: String,
    val cardCode: String,
)

@Entity(primaryKeys = ["balance", "cardCode", "gameTypeCode"])
data class BalanceCardCrossref(
    val gameTypeCode: String,
    val balance: String,
    val cardCode: String,
)

data class FormatEntity(
    @Embedded val cardFormat: FormatBaseEntity,

    @Relation(
        parentColumn = "gameTypeCode",
        entityColumn = "setCode",
        associateBy = Junction(FormatSetCrossref::class)
    )
    val includedSets: List<SetCode>,

    @Relation(
        parentColumn = "gameTypeCode",
        entityColumn = "cardCode",
        associateBy = Junction(FormatBannedCrossref::class)
    )
    val banned: List<CardCode>,

    @Relation(
        parentColumn = "gameTypeCode",
        entityColumn = "cardCode",
        associateBy = Junction(FormatRestrictedCrossref::class)
    )
    val restricted: List<CardCode>,

    @Relation(
        parentColumn = "gameTypeCode",
        entityColumn = "balance",
        associateBy = Junction(BalanceCardCrossref::class)
    )
    val balance: List<Balance>,
)

@Entity
data class FormatTimeEntity(
    @PrimaryKey val id: Int = 0,   //Yes, this is on purpose - only want one and replace always
    val timestamp: Long,
    val expiry: Long,
)
