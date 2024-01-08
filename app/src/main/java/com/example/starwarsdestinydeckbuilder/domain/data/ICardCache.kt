package com.example.starwarsdestinydeckbuilder.domain.data

import com.example.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import com.example.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import kotlinx.coroutines.flow.Flow
interface ICardCache {
    fun getCardByCode(code: String): Flow<Card?>
    fun getCardsBySet(code: String): Flow<List<Card>>
    fun getCardSets(): Flow<CardSetList>
    fun getFormats(): Flow<List<CardFormat>>
    suspend fun storeCards(cards: List<Card>)
    suspend fun storeCardSets(sets: CardSetList)
    suspend fun storeFormats(formats: List<CardFormat>)
}