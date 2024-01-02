package com.example.starwarsdestinydeckbuilder.domain.repositories

import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardbyCode(code: String): Flow<Resource<Card>>

    fun getCardSets(): Flow<Resource<List<CardSet>>>

    fun getCardsBySet(code: String): Flow<Resource<List<Card>>>

    fun getCardFormats(): Flow<Resource<List<CardFormat>>>
}