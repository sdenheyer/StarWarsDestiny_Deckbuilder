package com.stevedenheyer.starwarsdestinydeckbuilder.domain.repositories

import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardbyCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>>

    fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>>

    fun getCardsBySet(code: String, forceRemoteUpdate: Boolean): Flow<Resource<List<Card>>>

    fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>>

    fun findCards(query: String): Flow<Resource<List<Card>>>

    suspend fun createDeck(deck: Deck)

    fun getAllDecks(): Flow<List<Deck>>
}