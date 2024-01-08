package com.example.starwarsdestinydeckbuilder.data.remote.data

import android.util.Log
import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.example.starwarsdestinydeckbuilder.di.IoDispatcher
import com.example.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import com.example.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import retrofit2.Response
import java.io.IOException
import java.lang.NumberFormatException
import java.util.Date
import javax.inject.Inject

class CardNetwork @Inject constructor(private val cardService: CardService,
                            @IoDispatcher private val dispatcher: CoroutineDispatcher):ICardNetwork {
    override fun getCardByCode(code: String): Flow<ApiResponse<Card>> = flow {

        val apiResponse = try {
        val response = cardService.getCardByCode(code)
        if (response.body() != null) {
            val expiry = getExpiry(response.headers())
            ApiResponse.create(response, { it!!.toDomain().copy(timestamp = Date().time, expiry = expiry) })
        } else {
            ApiResponse.create(error = Throwable(response.message()))
        }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardsBySet(code: String): Flow<ApiResponse<List<Card>>> = flow {

        val apiResponse = try {
            val response = cardService.getCardsBySet(code)
          //  Log.d("SWD", "Response rec'd: ${response.body()?.size}")
            val expiry = getExpiry(response.headers())
            if (response.body() != null) {
                ApiResponse.create(response, { it!!.map { it.toDomain().copy(timestamp = Date().time, expiry = expiry) } })
            } else {
                ApiResponse.create(error = Throwable(response.message()))
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardSets(): Flow<ApiResponse<CardSetList>> = flow {

        val apiResponse = try {
            val response = cardService.getCardSets()
            if (response?.body() != null) {
                ApiResponse.create(response) {
                    val list = it!!.map { it.toDomain() }
                    val expiry = getExpiry(response.headers())
                    CardSetList(timestamp = Date().time, expiry = expiry, list)
                }
            } else {
                ApiResponse.create(error = Throwable(response.message()))
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)

    }.flowOn(dispatcher)

    override fun getFormats(): Flow<ApiResponse<CardFormatList>> = flow {

        val apiResponse = try {
            val response = cardService.getFormats()
          //  Log.d("SWD", response.body().toString())
            val list = response.body()?.map { it.toDomain() } ?: emptyList()
            val expiry = getExpiry(response.headers())
            if (response.body() != null) {
                ApiResponse.create(response) { CardFormatList(timestamp = Date().time, expiry = expiry, cardFormats = list) }
            } else {
                ApiResponse.create(error = Throwable(response.message()))
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)

    }.flowOn(dispatcher)
}

fun getExpiry(headers: Headers): Long {
    var expiry = 24 * 60 * 60 * 1000L
    val cacheControl = headers.values("cache-control")
    cacheControl.forEach {
        if (!it.endsWith("public", true)) {
            try {
               // Log.d("SWD", "Cache control header: $it")
                expiry = it.substringAfter("=").toLong() * 1000
                // expiry = 60 * 1000 //TEST VALUE
                Log.d("SWD", "Expiry: $expiry")
            } catch (e: NumberFormatException) {
                Log.d("SWD", "Couldn't find an integer in the string")
            }

        }
    }
    return expiry
}