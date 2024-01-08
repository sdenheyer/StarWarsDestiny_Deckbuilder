package com.example.starwarsdestinydeckbuilder.domain.data

import com.example.starwarsdestinydeckbuilder.data.remote.data.ApiResponse
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import com.example.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ICardNetwork {
    fun getCardByCode(code: String): Flow<ApiResponse<Card>>
    fun getCardsBySet(code: String): Flow<ApiResponse<List<Card>>>
    fun getCardSets(): Flow<ApiResponse<CardSetList>>
    fun getFormats(): Flow<ApiResponse<List<CardFormat>>>

}