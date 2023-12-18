package com.example.starwarsdestinydeckbuilder.domain.repositories

import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun getCardbyCode(code: String): Flow<Resource<Card>>

    suspend fun getCardSets(): List<CardSet>


}