package com.example.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class GameType(
    val name: String,
    @PrimaryKey val code: String,
)

@Entity
data class Legality(
    @PrimaryKey val legality: String,
)

@Entity
data class Balance(
    @PrimaryKey val balance: String,
)

@Entity(primaryKeys = ["code", "legality", "balance"])
data class Format(
    val code: String,
    val legality: String?,
    val balance: String?,
)


