package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import kotlinx.coroutines.flow.Flow
interface ICardCache {
    fun getCardByCode(code: String): Flow<Card?>
    fun getCardsBySet(code: String): Flow<List<Card>>
    fun getCardSets(): Flow<CardSetList>
    fun getFormats(): Flow<CardFormatList>
    suspend fun storeCards(cards: List<Card>)
    suspend fun storeCardSets(sets: CardSetList)
    suspend fun storeFormats(formats: CardFormatList)
}