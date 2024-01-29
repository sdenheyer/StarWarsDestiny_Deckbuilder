package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import kotlinx.coroutines.flow.Flow
interface ICardCache {
    fun getCardByCode(code: String): Flow<Card?>
    fun getCardsBySet(code: String): Flow<List<Card>>
    fun findCards(query: String): Flow<List<Card>>
    fun getCardSets(): Flow<CardSetList>
    fun getFormats(): Flow<CardFormatList>
    fun getDecks():Flow<List<Deck>>
    suspend fun storeCards(cards: List<Card>)
    suspend fun storeCardSets(sets: CardSetList)
    suspend fun storeFormats(formats: CardFormatList)
    suspend fun createDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)

}