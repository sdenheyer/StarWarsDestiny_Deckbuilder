package com.example.starwarsdestinydeckbuilder.data

import com.example.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardService
import com.example.starwarsdestinydeckbuilder.data.remote.data.networkBoundResource
import com.example.starwarsdestinydeckbuilder.di.IoDispatcher
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import com.example.starwarsdestinydeckbuilder.domain.repositories.CardRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardCache: CardCache,
    private val cardNetwork: CardNetwork,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): CardRepository {

    override fun getCardbyCode(code: String): Flow<Resource<Card>> {
        return networkBoundResource(
            fetchFromLocal =  { cardCache.getCardByCode(code) },
            shouldFetchFromRemote = { it == null },
            fetchFromRemote = { cardNetwork.getCardByCode(code) },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeCards(listOf(it)) } },
            onFetchFailed = { _, _ -> }

        ).flowOn(dispatcher)
    }

    override fun getCardSets(): Flow<Resource<List<CardSet>>> {
        return networkBoundResource(
            fetchFromLocal =  { cardCache.getCardSets() },
            shouldFetchFromRemote = { it.isNullOrEmpty() },
            fetchFromRemote = { cardNetwork.getCardSets() },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeCardSets(it) } },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardsBySet(code: String): Flow<Resource<List<Card>>> {
        return networkBoundResource(
            fetchFromLocal =  { cardCache.getCardsBySet(code) },
            shouldFetchFromRemote = { it.isNullOrEmpty() },
            fetchFromRemote = { cardNetwork.getCardsBySet(code) },
            processRemoteResponse = { },
            saveRemoteData = { coroutineScope.launch(dispatcher) { cardCache.storeCards(it) } },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }
}