package com.example.starwarsdestinydeckbuilder.domain.model

import java.net.URL

const val LEGALITY_ALLOWED = 1
const val LEGALITY_RESTRICTED = 2
const val LEGALITY_BANNED = 0

data class Card(
    val sides: List<String>?,
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
    val subtypes: List<Subtype>?,
    val position: Int,
    val code: String,
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
    val url: URL,
    val imageSrc: URL,
    val label: String,
    val cp: Int,
    val reprints: List<Int>?,
    val parallelDiceOf: List<Int>?,

    val legalityStandard: Int,
    val legalityTrilogy: Int,
    val legalityInfinite: Int,
    val legalityARHStandard: Int,

    val balance: String?,

    val timestamp: Long,
)

data class Subtype(
    val code: String,
    val name: String,
)
/*
data class Format(
    val name: String,
    val code: String,
    val isMember: Boolean,
    val balance: String
)*/
