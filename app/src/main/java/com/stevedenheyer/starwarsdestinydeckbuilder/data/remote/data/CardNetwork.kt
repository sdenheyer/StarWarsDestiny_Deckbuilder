package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.OperatorUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.di.IoDispatcher
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import java.io.IOException
import java.lang.NumberFormatException
import java.util.Date
import javax.inject.Inject

class CardNetwork @Inject constructor(private val cardService: CardService,
                            @IoDispatcher private val dispatcher: CoroutineDispatcher):ICardNetwork {
    override fun getCardByCode(lastModifiedDate: String, code: String): Flow<ApiResponse<Card>> = flow {

        val apiResponse = try {
        val response = cardService.getCardByCode(lastModifiedDate, code)
        if (response.body() != null) {
            val expiry = getExpiry(response.headers())
            ApiResponse.create(response) {
                it!!.toDomain().copy(timestamp = Date().time, expiry = expiry)
            }
        } else {
            ApiResponse.create(error = Throwable(response.message()), response.code())
        }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardsBySet(lastModifiedDate: String, code: String): Flow<ApiResponse<List<Card>>> = flow {

        val apiResponse = try {
            val response = cardService.getCardsBySet(lastModifiedDate, code)
          //  Log.d("SWD", "Response rec'd: ${response.body()?.size}")
            val expiry = getExpiry(response.headers())
            if (response.body() != null) {
                ApiResponse.create(response) { list ->
                    list!!.map {
                        it.toDomain().copy(timestamp = Date().time, expiry = expiry)
                    }
                }
            } else {
                ApiResponse.create(error = Throwable(response.message()), response.code())
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    override fun getCardSets(lastModifiedDate: String): Flow<ApiResponse<CardSetList>> = flow {

        val apiResponse = try {
            val response = cardService.getCardSets(lastModifiedDate)
            if (response.body() != null) {
                ApiResponse.create(response) { list ->
                    val newList = list!!.map { it.toDomain() }
                    val expiry = getExpiry(response.headers())
                    CardSetList(timestamp = Date().time, expiry = expiry, newList)
                }
            } else {
                ApiResponse.create(error = Throwable(response.message()), response.code())
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)

    }.flowOn(dispatcher)

    override fun getFormats(lastModifiedDate: String): Flow<ApiResponse<CardFormatList>> = flow {

        val apiResponse = try {
            val response = cardService.getFormats(lastModifiedDate)
          //  Log.d("SWD", response.body().toString())
            val list = response.body()?.map { it.toDomain() } ?: emptyList()
            val expiry = getExpiry(response.headers())
            if (response.body() != null) {
                ApiResponse.create(response) { CardFormatList(timestamp = Date().time, expiry = expiry, cardFormats = list) }
            } else {
                ApiResponse.create(error = Throwable(response.message()), response.code())
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)

    }.flowOn(dispatcher)

    override fun findCards(query: QueryUi): Flow<ApiResponse<List<Card>>> = flow {

        val apiResponse = try {
            val response = cardService.findCards(getRemoteQueryString(query))
            //  Log.d("SWD", "Response rec'd: ${response.body()?.size}")
            val expiry = getExpiry(response.headers())
            if (response.body() != null) {
                ApiResponse.create(response) { list ->
                    list!!.map {
                        it.toDomain().copy(timestamp = Date().time, expiry = expiry)
                    }
                }
            } else {
                ApiResponse.create(error = Throwable(response.message()), response.code())
            }
        } catch(e: IOException) {
            ApiResponse.create(error = Throwable("Network error"))
        }
        emit(apiResponse)
    }.flowOn(dispatcher)

    private fun getRemoteQueryString(query: QueryUi): String {
        val queryString = StringBuilder()

        query.run {
            if (byCardName.isNotBlank()) {
                queryString.append("$byCardName ")
            }

            if (bySubtype.isNotBlank()) {
                queryString.append("b:$bySubtype")
            }

            if (byColors.size < 4) {
                queryString.append("f:")
                byColors.forEachIndexed { i, color ->
                    queryString.append(color)
                    if (i < query.byColors.size - 1)
                        queryString.append("|")
                    else
                        queryString.append(" ")
                }
            }

            byHealth.run {
                if (number != 0) {
                    queryString.append("h")
                    when (operator) {
                        OperatorUi.LESS_THAN -> queryString.append("<")
                        OperatorUi.MORE_THAN -> queryString.append(">")
                        OperatorUi.EQUALS -> queryString.append(":")
                    }
                    queryString.append("$number ")
                }
            }

            if (byFormat.isNotBlank()) {
                queryString.append("m:${byFormat} ")
            }

            byCost.run {
                if (number != 0) {
                    queryString.append("o")
                    when (operator) {
                        OperatorUi.LESS_THAN -> queryString.append("<")
                        OperatorUi.MORE_THAN -> queryString.append(">")
                        OperatorUi.EQUALS -> queryString.append(":")
                    }
                    queryString.append("$number ")
                }
            }

            if (bySet.isNotBlank()) {
                queryString.append("s:${bySet} ")
            }

            if (byType.isNotBlank()) {
                queryString.append("t:${byType} ")
            }

            if (byUnique) {
                queryString.append("u:1 ")
            }

            if (byCardText.isNotBlank()) {
                queryString.append("x:${byCardText} ")
            }
        }

        return queryString.toString()
    }

    private fun getExpiry(headers: Headers): Long {
        var expiry = 24 * 60 * 60 * 1000L
        val cacheControl = headers.values("cache-control")
        cacheControl.forEach {
            if (!it.endsWith("public", true)) {
                try {
                    // Log.d("SWD", "Cache control header: $it")
                    expiry = it.substringAfter("=").toLong() * 1000
                   // Log.d("SWD", "Expiry: $expiry")
                } catch (e: NumberFormatException) {
                    Log.d("SWD", "Couldn't find an integer in the string")
                }

            }
        }
        return expiry
    }
}

