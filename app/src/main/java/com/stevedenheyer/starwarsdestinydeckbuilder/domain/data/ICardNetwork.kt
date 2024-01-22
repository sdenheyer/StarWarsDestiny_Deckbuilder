package com.stevedenheyer.starwarsdestinydeckbuilder.domain.data

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import kotlinx.coroutines.flow.Flow

interface ICardNetwork {
    fun getCardByCode(code: String): Flow<ApiResponse<Card>>
    fun getCardsBySet(code: String): Flow<ApiResponse<List<Card>>>
    fun getCardSets(): Flow<ApiResponse<CardSetList>>
    fun getFormats(): Flow<ApiResponse<CardFormatList>>
    fun findCards(query: String): Flow<ApiResponse<List<Card>>>

}