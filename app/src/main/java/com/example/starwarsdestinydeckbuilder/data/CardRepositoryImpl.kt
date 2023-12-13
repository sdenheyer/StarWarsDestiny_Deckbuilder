package com.example.starwarsdestinydeckbuilder.data

import com.example.starwarsdestinydeckbuilder.data.retrofit.CardAPI
import com.example.starwarsdestinydeckbuilder.data.retrofit.model.CardJS
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.repositories.CardRepository

class CardRepositoryImpl constructor(private val CardAPI: CardAPI): CardRepository {
    override suspend fun getCardbyCode(code: String): CardJS {
        val result = CardAPI.getCardByCode(code)
        if (result.isSuccessful) {
            return result.body() ?: CardJS("null")
        } else {
            return CardJS("${result.errorBody()}")
        }
    }
}