package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

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
    val code: String,
    val ttsCardID: Int,
    val name: String,
    val subtitle: String?,
    val cost: Int?,    //TODO:  Change this to string
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
    val reprints: List<CodeOrCard<*>> = emptyList(),
    val parallelDiceOf: List<CodeOrCard<*>> = emptyList(),

    //val reprintCards: List<Card> = emptyList(),
    //val parellelDiceCards: List<Card> = emptyList(),

    val formats: List<Format>? = null,

    val timestamp: Long,
    val expiry: Long = 0,
)

data class Subtype(
    val code: String,
    val name: String,
)

data class Format(
    val gameType: String,
    val legality: String? = null,
    val balance: String? = null,
)