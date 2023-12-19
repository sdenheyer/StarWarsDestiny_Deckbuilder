package com.example.starwarsdestinydeckbuilder.data.remote.data

import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.di.IoDispatcher
import com.example.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class CardNetwork @Inject constructor(private val cardService: CardService,
                            @IoDispatcher private val dispatcher: CoroutineDispatcher):ICardNetwork {
    override fun getCardByCode(code: String): Flow<ApiResponse<Card>> = flow {

        val apiResponse = try {
        val response = cardService.getCardByCode(code)
        if (response.body() != null) {
            ApiResponse.create(response, { it!!.toDomain() })
        } else {
            ApiResponse.create(error = Throwable("No data"))
        }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardsBySet(code: String): Flow<ApiResponse<List<Card>>> = flow {

        val apiResponse = try {
            val response = cardService.getCardsBySet(code)
            if (response.body() != null) {
                ApiResponse.create(response, { it!!.map { it.toDomain() } })
            } else {
                ApiResponse.create(error = Throwable("No data"))
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardSets(): Flow<ApiResponse<List<CardSet>>> = flow {

        val apiResponse = try {
            val response = cardService.getCardSets()
            if (response?.body() != null) {
                ApiResponse.create(response, { it!!.map { it.toDomain() } })
            } else {
                ApiResponse.create(error = Throwable("No data"))
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)

    }.flowOn(dispatcher)
}