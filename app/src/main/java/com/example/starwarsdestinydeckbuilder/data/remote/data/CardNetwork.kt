package com.example.starwarsdestinydeckbuilder.data.remote.data

import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class CardNetwork @Inject constructor(private val cardService: CardService):ICardNetwork {
    override fun getCardByCode(code: String): Flow<ApiResponse<Card>> = flow {

        val response = cardService.getCardByCode(code)
        if (response.body() != null) {
            ApiResponse.create(response, { it!!.toDomain() })
        }
    }

    override suspend fun getCardsBySet(code: String): Flow<Response<List<CardDTO>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCardSets(): Flow<Response<List<CardSetDTO>>> {
        TODO("Not yet implemented")
    }
}