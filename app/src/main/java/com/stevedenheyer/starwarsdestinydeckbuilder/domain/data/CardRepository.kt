package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SavedQueriesUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardByCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>>

    fun getCardBySetAndPosition(set: String, position: Int): Flow<Resource<Card?>>

    fun getCardsByCodes(vararg values: CardOrCode): Flow<Resource<List<CardOrCode>>>

    fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>>

    fun getCardsBySet(code: String, forceRemoteUpdate: Boolean): Flow<Resource<List<Card>>>

    fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>>

    fun fetchSavedQueries(): Flow<SavedQueriesUi>

    suspend fun updateSavedNameQueries(newQuery: String)

    suspend fun updateSavedSubtypeQueries(newQuery: String)

    suspend fun updateSavedTextQueries(newQuery: String)

    fun findCards(query: QueryUi): Flow<Resource<List<Card>>>

    suspend fun createDeck(deck: Deck)

    fun getAllDecks(): Flow<List<Deck>>

    suspend fun updateDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck, slot: Slot)

    suspend fun updateDeck(deck: Deck, char: CharacterCard)

    suspend fun getDeck(deckName: String): Deck

    fun deleteDeck(deck:Deck)

    fun getOwnedCards():Flow<List<OwnedCard>>

    suspend fun insertOwnedCards(vararg cards:OwnedCard)

    fun sortStateFlow():Flow<SortUi>

    suspend fun setSortByState(sortState: SortState, gameType: String)
}