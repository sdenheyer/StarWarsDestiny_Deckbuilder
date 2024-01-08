package com.example.starwarsdestinydeckbuilder.domain.repositories

import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import com.example.starwarsdestinydeckbuilder.domain.model.CardSetList
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun getCardbyCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>>

    fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>>

    fun getCardsBySet(code: String, forceRemoteUpdate: Boolean): Flow<Resource<List<Card>>>

    fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>>
}