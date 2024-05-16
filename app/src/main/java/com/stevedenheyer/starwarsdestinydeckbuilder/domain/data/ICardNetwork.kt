package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import kotlinx.coroutines.flow.Flow

interface ICardNetwork {
    fun getCardByCode(lastModifiedDate: String, code: String): Flow<ApiResponse<Card>>
    fun getCardsBySet(lastModifiedDate: String, code: String): Flow<ApiResponse<List<Card>>>
    fun getCardSets(lastModifiedDate: String): Flow<ApiResponse<CardSetList>>
    fun getFormats(lastModifiedDate: String): Flow<ApiResponse<CardFormatList>>
    fun findCards(query: QueryUi): Flow<ApiResponse<List<Card>>>

}