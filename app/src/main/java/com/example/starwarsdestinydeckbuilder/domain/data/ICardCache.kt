package com.example.starwarsdestinydeckbuilder.domain.data

import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow
interface ICardCache {
    fun getCardByCode(code: String):Flow<Card>
    fun getCardsBySet(code: String):Flow<List<Card>>
    suspend fun getCardSets():Flow<List<CardSet>>
    suspend fun storeCards(cards: List<Card>)
    suspend fun storeCardSets(sets: List<CardSet>)
}