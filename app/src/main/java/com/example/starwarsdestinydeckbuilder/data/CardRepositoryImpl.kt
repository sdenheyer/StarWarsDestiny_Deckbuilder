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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardCache: CardCache,
    private val cardNetwork: CardNetwork,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): CardRepository {


    /* override suspend fun getCardbyCode(code: String): Card {
        val result = cardService.getCardByCode(code)
        if (result.isSuccessful && result.body() != null) {
            return result.body()!!.toDomain()
        } else {
            return CardDTO.testCard.copy(name = "ERROR!").toDomain()
        }
    }

    override suspend fun getCardSets(): List<CardSet> {
        val result = cardService.getCardSets()
        if (result.isSuccessful && result.body() != null) {
            return result.body()!!.map { it.toDomain() }
        } else {
            return emptyList()
        }
    }

*/
    override suspend fun getCardbyCode(code: String): Flow<Resource<Card>> {
        return networkBoundResource(
            fetchFromLocal =  { cardCache.getCardByCode(code) },
            shouldFetchFromRemote = { it == null },
            fetchFromRemote = { cardNetwork.getCardByCode(code) },
            processRemoteResponse = { },
            saveRemoteData = {  },
            onFetchFailed = { _, _ -> }

        ).flowOn(dispatcher)
    }

    override suspend fun getCardSets(): List<CardSet> {
        TODO("Not yet implemented")
    }
}