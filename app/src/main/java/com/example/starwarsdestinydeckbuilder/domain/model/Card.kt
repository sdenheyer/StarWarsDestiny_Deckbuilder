package com.example.starwarsdestinydeckbuilder.domain.model

import java.net.URL

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
    val code: Int,
    val ttsCardID: Int,
    val name: String,
    val subtitle: String?,
    val cost: Int?,
    val health: Int?,
    val points: String,
    val text: String,
    val deckLimit: Int,
    val flavour: String,
    val illustrator: String,
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

    val formats: List<Format>
)

data class Subtype(
    val code: String,
    val name: String,
)

data class Format(
    val name: String,
    val code: String,
    val isMember: Boolean,
    val balance: String
)
