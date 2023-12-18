package com.example.starwarsdestinydeckbuilder.domain.data

import com.example.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ICardNetwork {
    fun getCardByCode(code: String): Flow<ApiResponse<Card>>
    suspend fun getCardsBySet(code: String): Flow<Response<List<CardDTO>>>
    suspend fun getCardSets(): Flow<Response<List<CardSetDTO>>>

}