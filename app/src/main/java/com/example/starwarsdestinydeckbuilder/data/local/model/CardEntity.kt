package com.example.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardEntity(
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
   // val subtypes: List<Subtype>?,  TODO: Set this up as an Entity
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
    // val reprints: List<Int>?,  Setup as One-to-Many
    // val parallelDiceOf: List<Int>?, Setup as One-to-Many

    /*val legalityStandard: Int,   //TODO:  Set these up as entities
    val legalityTrilogy: Int,
    val legalityInfinite: Int,
    val legalityARHStandard: Int,*/

    val balance: String?
)
