package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import kotlinx.coroutines.flow.Flow
interface ICardCache {
    fun getCardByCode(code: String): Flow<Card?>
    fun getCardBySetAndPosition(set: String, position: Int): Flow<Card?>
    suspend fun getCardsByCodes(vararg values: CardOrCode): List<CardOrCode>
    fun getCardsBySet(code: String): Flow<List<Card>>
    fun findCards(query: QueryUi): Flow<List<Card>>
    fun getCardSets(): Flow<CardSetList>
    fun getFormats(): Flow<CardFormatList>
    fun getDecks():Flow<List<Deck>>
    suspend fun storeCards(cards: List<Card>)
    suspend fun storeCardSets(sets: CardSetList)
    suspend fun storeFormats(formatlist: CardFormatList)
    suspend fun createDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck, slot: Slot)
    suspend fun updateDeck(deck: Deck, char: CharacterCard)
    fun deleteDeck(deck: Deck)
    suspend fun getDeck(name: String):Deck
    fun getOwnedCards():Flow<List<OwnedCard>>
    suspend fun storeOwnedCards(vararg cards:OwnedCard)
}