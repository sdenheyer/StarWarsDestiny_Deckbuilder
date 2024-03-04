package com.stevedenheyer.starwarsdestinydeckbuilder.domain.repositories

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardbyCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>>

    fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>>

    fun getCardsBySet(code: String, forceRemoteUpdate: Boolean): Flow<Resource<List<Card>>>

    fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>>

    fun findCards(query: QueryUi): Flow<Resource<List<Card>>>

    suspend fun createDeck(deck: Deck)

    fun getAllDecks(): Flow<List<Deck>>

    suspend fun updateDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck, slot: Slot)

    suspend fun updateDeck(deck: Deck, char: CharacterCard)

    suspend fun getDeck(deckName: String): Deck

    fun getOwnedCards():Flow<List<OwnedCard>>

    suspend fun insertOwnedCards(vararg cards:OwnedCard)
}