package com.stevedenheyer.starwarsdestinydeckbuilder.data

import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.networkBoundResource
import com.stevedenheyer.starwarsdestinydeckbuilder.di.IoDispatcher
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.repositories.CardRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardCache: CardCache,
    private val cardNetwork: CardNetwork,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CardRepository {

    override fun getCardbyCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardByCode(code) },
            shouldFetchFromRemote = { it == null ||
                    (forceRemoteUpdate) ||
                    (Date().time - (it.timestamp) > (it.expiry))},
            fetchFromRemote = { cardNetwork.getCardByCode(code) },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeCards(listOf(it)) } },
            onFetchFailed = { _, _ -> }

        ).flowOn(dispatcher)
    }

    override fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardSets() },
            shouldFetchFromRemote = {
                (it?.cardSets.isNullOrEmpty()) ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it?.timestamp ?: 0L) > (it?.expiry ?: 0L))
            },
            fetchFromRemote = { cardNetwork.getCardSets() },
            processRemoteResponse = { },
            saveRemoteData = {
                coroutineScope.launch(dispatcher) {
                    cardCache.storeCardSets(it)
                }
            },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardsBySet(code: String, forceRemoteUpdate: Boolean): Flow<Resource<List<Card>>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardsBySet(code) },
            shouldFetchFromRemote = { it.isNullOrEmpty() ||
                    (forceRemoteUpdate) },
            fetchFromRemote = { cardNetwork.getCardsBySet(code) },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeCards(it) } },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getFormats() },
            shouldFetchFromRemote = { it?.cardFormats.isNullOrEmpty() ||
                    (forceRemoteUpdate) ||
                    (Date().time - (it?.timestamp ?: 0L) > (it?.expiry ?: 0L))},
            fetchFromRemote = { cardNetwork.getFormats() },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeFormats(it) } },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun findCards(query: String): Flow<Resource<List<Card>>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.findCards(query) },
            shouldFetchFromRemote = { true },
            fetchFromRemote = { cardNetwork.findCards(query) },
            processRemoteResponse = { },
            saveRemoteData =  { coroutineScope.launch(dispatcher) { cardCache.storeCards(it) }},
            onFetchFailed = { _, _ -> }
        )
    }

    override suspend fun createDeck(deck: Deck) {
        cardCache.createDeck(deck)
    }

    override fun getAllDecks(): Flow<List<Deck>> = cardCache.getDecks()

    override suspend fun updateDeck(deck: Deck, slot: Slot) {
        cardCache.updateDeck(deck, slot)
    }

    override suspend fun getDeck(deckName: String): Deck = cardCache.getDeck(deckName)
}